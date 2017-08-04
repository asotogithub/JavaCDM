package trueffect.truconnect.api.oauth.mybatis;

import trueffect.truconnect.api.oauth.gson.AccessTokenDeserializer;
import trueffect.truconnect.api.oauth.gson.AccessTokenSerializer;
import trueffect.truconnect.api.oauth.gson.AuthenticationDeserializer;
import trueffect.truconnect.api.oauth.gson.AuthenticationSerializer;
import trueffect.truconnect.api.oauth.gson.GrantedAuthorityDeserializer;
import trueffect.truconnect.api.oauth.gson.GrantedAuthoritySerializer;
import trueffect.truconnect.api.oauth.gson.RefreshTokenDeserializer;
import trueffect.truconnect.api.oauth.gson.RefreshTokenSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

/**
 * Created by richard.jaldin on 11/18/2015.
 */
public class CustomJdbcTokenStore implements TokenStore {

    private static final Logger LOG = LoggerFactory.getLogger(CustomJdbcTokenStore.class);

    private final String insertAccessTokenSql = "insert into TE_XLS.oauth_access_token (token_id, token, authentication_id, user_name, client_id, authentication, refresh_token) values (?, ?, ?, ?, ?, ?, ?)";
    private final String selectAccessTokenSql = "select token_id, token from TE_XLS.oauth_access_token where token_id = ?";
    private final String selectAccessTokenAuthenticationSql = "select token_id, authentication from TE_XLS.oauth_access_token where token_id = ?";
    private final String selectAccessTokenFromAuthenticationSql = "select token_id, token from TE_XLS.oauth_access_token where authentication_id = ?";
    private final String selectAccessTokensFromUserNameSql = "select token_id, token from TE_XLS.oauth_access_token where user_name = ?";
    private final String selectAccessTokensFromClientIdSql = "select token_id, token from TE_XLS.oauth_access_token where client_id = ?";
    private final String deleteAccessTokenSql = "delete from TE_XLS.oauth_access_token where token_id = ?";
    private final String deleteAccessTokenFromRefreshTokenSql = "delete from TE_XLS.oauth_access_token where refresh_token = ?";
    private final String insertRefreshTokenSql = "insert into TE_XLS.oauth_refresh_token (token_id, token, authentication) values (?, ?, ?)";
    private final String selectRefreshTokenSql = "select token_id, token from TE_XLS.oauth_refresh_token where token_id = ?";
    private final String selectRefreshTokenAuthenticationSql = "select token_id, authentication from TE_XLS.oauth_refresh_token where token_id = ?";
    private final String deleteRefreshTokenSql = "delete from TE_XLS.oauth_refresh_token where token_id = ?";

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private Gson gson;
    private final JdbcTemplate jdbcTemplate;

    public CustomJdbcTokenStore(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(OAuth2AccessToken.class, new AccessTokenSerializer());
        builder.registerTypeAdapter(OAuth2AccessToken.class, new AccessTokenDeserializer());
        builder.registerTypeAdapter(DefaultOAuth2AccessToken.class, new AccessTokenSerializer());
        builder.registerTypeAdapter(GrantedAuthority.class, new GrantedAuthoritySerializer());
        builder.registerTypeAdapter(GrantedAuthority.class, new GrantedAuthorityDeserializer());
        builder.registerTypeAdapter(Authentication.class, new AuthenticationSerializer());
        builder.registerTypeAdapter(Authentication.class, new AuthenticationDeserializer());
        builder.registerTypeAdapter(OAuth2RefreshToken.class, new RefreshTokenSerializer());
        builder.registerTypeAdapter(OAuth2RefreshToken.class, new RefreshTokenDeserializer());
        gson = builder.create();
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;

        String key = authenticationKeyGenerator.extractKey(authentication);
        try {
            accessToken = jdbcTemplate.queryForObject(selectAccessTokenFromAuthenticationSql,
                    new RowMapper<OAuth2AccessToken>() {
                        public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return deserializeAccessToken(rs.getString(2));
                        }
                    }, key);
        }
        catch (EmptyResultDataAccessException e) {
            LOG.warn("Failed to find access token for authentication {}", authentication);
        }
        catch (IllegalArgumentException e) {
            LOG.error("Could not extract access token for authentication {}", authentication, e);
        }

        if (accessToken != null
                && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken.getValue());
            // Keep the store consistent (maybe the same user is represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        jdbcTemplate.update(insertAccessTokenSql, new Object[] { extractTokenKey(token.getValue()),
                serializeAccessToken(token), authenticationKeyGenerator.extractKey(authentication),
                authentication.isClientOnly() ? null : authentication.getName(),
                authentication.getOAuth2Request().getClientId(),
                serializeAuthentication(authentication), extractTokenKey(refreshToken) }, new int[] {
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR });
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;

        try {
            accessToken = jdbcTemplate.queryForObject(selectAccessTokenSql, new RowMapper<OAuth2AccessToken>() {
                public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return deserializeAccessToken(rs.getString(2));
                }
            }, extractTokenKey(tokenValue));
        }
        catch (EmptyResultDataAccessException e) {
            LOG.warn("Failed to find access token for token {}", tokenValue);
        }
        catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize access token for {}", tokenValue, e);
            removeAccessToken(tokenValue);
        }

        return accessToken;
    }

    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    public void removeAccessToken(String tokenValue) {
        jdbcTemplate.update(deleteAccessTokenSql, extractTokenKey(tokenValue));
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication authentication = null;

        try {
            authentication = jdbcTemplate.queryForObject(selectAccessTokenAuthenticationSql,
                    new RowMapper<OAuth2Authentication>() {
                        public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return deserializeAuthentication(rs.getString(2));
                        }
                    }, extractTokenKey(token));
        } catch (EmptyResultDataAccessException e) {
            LOG.warn("Failed to find access token for token {}", token);
        }
        catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize authentication for {}", token, e);
            removeAccessToken(token);
        }

        return authentication;
    }

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        jdbcTemplate.update(insertRefreshTokenSql, new Object[]{extractTokenKey(refreshToken.getValue()),
                serializeRefreshToken(refreshToken),
                serializeAuthentication(authentication)}, new int[]{Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR});
    }

    public OAuth2RefreshToken readRefreshToken(String token) {
        OAuth2RefreshToken refreshToken = null;

        try {
            refreshToken = jdbcTemplate.queryForObject(selectRefreshTokenSql, new RowMapper<OAuth2RefreshToken>() {
                public OAuth2RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return deserializeRefreshToken(rs.getString(2));
                }
            }, extractTokenKey(token));
        }
        catch (EmptyResultDataAccessException e) {
            LOG.warn("Failed to find refresh token for token {}", token);
        }
        catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize refresh token for token {}", token, e);
            removeRefreshToken(token);
        }

        return refreshToken;
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }

    public void removeRefreshToken(String token) {
        jdbcTemplate.update(deleteRefreshTokenSql, extractTokenKey(token));
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
        OAuth2Authentication authentication = null;

        try {
            authentication = jdbcTemplate.queryForObject(selectRefreshTokenAuthenticationSql,
                    new RowMapper<OAuth2Authentication>() {
                        public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return deserializeAuthentication(rs.getString(2));
                        }
                    }, extractTokenKey(value));
        }
        catch (EmptyResultDataAccessException e) {
            LOG.warn("Failed to find access token for token {}", value);
        }
        catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize access token for {}", value, e);
            removeRefreshToken(value);
        }

        return authentication;
    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        jdbcTemplate.update(deleteAccessTokenFromRefreshTokenSql, new Object[] { extractTokenKey(refreshToken) },
                new int[] { Types.VARCHAR });
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

        try {
            accessTokens = jdbcTemplate.query(selectAccessTokensFromClientIdSql, new SafeAccessTokenRowMapper(),
                    clientId);
        }
        catch (EmptyResultDataAccessException e) {
            LOG.warn("Failed to find access token for clientId {}", clientId);
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

        try {
            accessTokens = jdbcTemplate.query(selectAccessTokensFromUserNameSql, new SafeAccessTokenRowMapper(),
                    userName);
        }
        catch (EmptyResultDataAccessException e) {
            LOG.warn("Failed to find access token for userName {}", userName);
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {
        List<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        for (OAuth2AccessToken token : accessTokens) {
            if (token != null) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

    private final class SafeAccessTokenRowMapper implements RowMapper<OAuth2AccessToken> {
        public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                return deserializeAccessToken(rs.getString(2));
            }
            catch (IllegalArgumentException e) {
                String token = rs.getString(1);
                jdbcTemplate.update(deleteAccessTokenSql, token);
                return null;
            }
        }
    }

    protected String serializeAccessToken(OAuth2AccessToken token) {
        return gson.toJson(token, OAuth2AccessToken.class);
    }

    protected String serializeAuthentication(OAuth2Authentication authentication) {
        return gson.toJson(authentication, OAuth2Authentication.class);
    }

    protected String serializeRefreshToken(OAuth2RefreshToken token) {
        return gson.toJson(token, OAuth2RefreshToken.class);
    }

    protected OAuth2AccessToken deserializeAccessToken(String token) {
        return gson.fromJson(token, OAuth2AccessToken.class);
    }

    protected OAuth2Authentication deserializeAuthentication(String authentication) {
        return gson.fromJson(authentication, OAuth2Authentication.class);
    }

    protected OAuth2RefreshToken deserializeRefreshToken(String token) {
        return gson.fromJson(token, OAuth2RefreshToken.class);
    }
}
