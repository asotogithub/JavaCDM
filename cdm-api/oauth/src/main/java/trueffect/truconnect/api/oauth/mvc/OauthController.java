package trueffect.truconnect.api.oauth.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import trueffect.truconnect.api.oauth.APIClientDetailsService;
import trueffect.truconnect.api.oauth.APIUserApprovalHandler;
import trueffect.truconnect.api.oauth.OauthAuthorization;
import trueffect.truconnect.api.oauth.TokenHandler;
import trueffect.truconnect.api.oauth.encoding.EncryptUtil;
import trueffect.truconnect.api.oauth.model.APIOAuth2AccessToken;
import trueffect.truconnect.api.oauth.model.OauthKey;
import trueffect.truconnect.api.oauth.model.SuccessResponse;
import trueffect.truconnect.api.resources.ResourceUtil;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Rambert Rioja
 */
@Controller("oauthController")
public class OauthController {

    private DefaultTokenServices tokenServices;
    private APIClientDetailsService apiClientDetailsService;
    private TokenStore tokenStore;
    private APIUserApprovalHandler userApprovalHandler;
    private TokenHandler tokenHandler;
    private OauthAuthorization autho;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public OauthController() {
        this.apiClientDetailsService = new APIClientDetailsService();
        this.tokenHandler = new TokenHandler();
        this.autho = new OauthAuthorization();
    }

    @RequestMapping(value = "/cache_approvals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startCaching() throws Exception {
        userApprovalHandler.setUseApprovalStore(true);
    }

    @RequestMapping("/uncache_approvals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stopCaching() throws Exception {
        userApprovalHandler.setUseApprovalStore(false);
    }

    @RequestMapping("/users/{user}/tokens")
    @ResponseBody
    public Collection<OAuth2AccessToken> listTokensForUser(@PathVariable String user,
            Principal principal)
            throws Exception {
        checkResourceOwner(user, principal);
        return tokenStore.findTokensByUserName(user);
    }

    @RequestMapping(value = "/users/{user}/tokens/{token}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE},
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> revokeToken(@PathVariable String user,
            @PathVariable String token, Principal principal)
            throws Exception {
        checkResourceOwner(user, principal);
        if (tokenServices.revokeToken(token)) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/clients/{client}/tokens",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    @ResponseBody
    public List<APIOAuth2AccessToken> listTokensForClient(@PathVariable String client) throws Exception {
        List<APIOAuth2AccessToken> tokens = new ArrayList<APIOAuth2AccessToken>();
        for (OAuth2AccessToken token : tokenStore.findTokensByClientId(client)) {
            tokens.add(new APIOAuth2AccessToken(token));
        }
        return tokens;
    }

    private void checkResourceOwner(String user, Principal principal) {
        if (principal instanceof OAuth2Authentication) {
            OAuth2Authentication authentication = (OAuth2Authentication) principal;
            if (!authentication.isClientOnly() && !user.equals(principal.getName())) {
                throw new AccessDeniedException(String.format("User '%s' cannot obtain tokens for user '%s'",
                        principal.getName(), user));
            }
        }
    }

    public void setUserApprovalHandler(APIUserApprovalHandler userApprovalHandler) {
        this.userApprovalHandler = userApprovalHandler;
    }

    public void setTokenServices(DefaultTokenServices tokenServices) {
        this.tokenServices = tokenServices;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @RequestMapping(value = "/unauthorized",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    @ResponseBody
    public ResponseEntity<String> getUnauthorized() {
        ResponseEntity response = new ResponseEntity("Unauthorized", HttpStatus.FORBIDDEN);
        return response;
    }

    @RequestMapping(value = "/loggedout",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    @ResponseBody
    public ResponseEntity getLoggedout(Principal principal) {
        return new ResponseEntity(new SuccessResponse("Logged out"), HttpStatus.OK);
    }

    @RequestMapping(value = "/verifytoken",
                    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE},
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity verifyToken(@RequestHeader(value = "Authorization") String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        accessToken = tokenHandler.parseAccessToken(accessToken);
        OAuth2AccessToken oa2at;
        try {
            oa2at = tokenServices.readAccessToken(accessToken);
        } catch (Exception e) {
            headers.add("WWW-Authenticate",
                    "Bearer error=\"invalid_token\", error_description=\"Invalid token\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        }
        if (oa2at == null) {
            headers.add("WWW-Authenticate",
                        "Bearer error=\"invalid_token\", error_description=\"Invalid token\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        } else if (oa2at.isExpired()) {
            headers.add("WWW-Authenticate",
                        "Bearer error=\"invalid_token\", "
                        + "error_description=\"The access token has expired\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity(headers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/checktoken",
                    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE},
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity checkToken(@RequestHeader(value = "Authorization") String accessToken)
            throws Exception {
        HttpHeaders headers = new HttpHeaders();
        accessToken = tokenHandler.parseAccessToken(accessToken);
        OAuth2AccessToken oa2at;
        User principal;
        try {
            oa2at = tokenServices.readAccessToken(accessToken);
            OAuth2Authentication authentication = tokenStore.readAuthentication(oa2at);
            principal = (User)authentication.getPrincipal();
        } catch (Exception e) {
            headers.add("WWW-Authenticate",
                    "Bearer error=\"invalid_token\", error_description=\"Invalid token\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        }
        if (oa2at == null) {
            headers.add("WWW-Authenticate",
                        "Bearer error=\"invalid_token\", error_description=\"Invalid token\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        } else if (oa2at.isExpired()) {
            headers.add("WWW-Authenticate",
                        "Bearer error=\"invalid_token\", "
                        + "error_description=\"The access token has expired\""
            );
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        }
        int leftToExpire = Integer.valueOf(ResourceUtil.get("oauth.expiry.time.notice", "10"));
        if (oa2at.getExpiresIn() < leftToExpire) {
            headers.add("WWW-Authenticate",
                        "Bearer error=\"valid_token\", "
                        + "error_description=\"The access token is close to expire\""
            );
        }
        String userIdEncrypted = EncryptUtil.encryptAES(principal.getUsername());
        OauthKey
                key =
                new OauthKey(userIdEncrypted,
                             oa2at.getAdditionalInformation().get("tpws").toString());
        logger.debug("Checked Token {}", principal.getUsername());
        return new ResponseEntity(key, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/authorizetoken/{roles}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE},
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity authorizeToken(@PathVariable String roles,
            @RequestHeader(value = "Authorization") String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        accessToken = tokenHandler.parseAccessToken(accessToken);
        OAuth2AccessToken oa2at;
        User principal;
        try {
            oa2at = tokenServices.readAccessToken(accessToken);
            OAuth2Authentication authentication = tokenStore.readAuthentication(oa2at);
            principal = (User)authentication.getPrincipal();
        } catch (Exception e) {
            headers.add("WWW-Authenticate",
                    "Bearer error=\"invalid_token\", error_description=\"Invalid token\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        }
        if (oa2at == null) {
            headers.add("WWW-Authenticate",
                    "Bearer error=\"invalid_token\", error_description=\"Invalid token\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        } else if (oa2at.isExpired()) {
            headers.add("WWW-Authenticate",
                    "Bearer error=\"invalid_token\", "
                    + "error_description=\"The access token has expired\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        }
        if (this.autho.includeAuthority(roles, principal, apiClientDetailsService)) {
            int leftToExpire = Integer.valueOf(ResourceUtil.get("oauth.expiry.time.notice", "10"));
            if (oa2at.getExpiresIn() < leftToExpire) {
                headers.add("WWW-Authenticate",
                        "Bearer error=\"valid_token\", "
                        + "error_description=\"The access token is close to expire\"");
            }
            String userIdEncrypted = EncryptUtil.encryptAES(principal.getUsername());
            OauthKey key = new OauthKey(userIdEncrypted, oa2at.getAdditionalInformation().get("tpws").toString());
            return new ResponseEntity(key, headers, HttpStatus.OK);
        } else {
            headers.add("WWW-Authenticate",
                    "Bearer error=\"permission_denied\", "
                    + "error_description=\"You do not have permission to access\"");
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/accesstoken",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    @ResponseBody
    public ResponseEntity getToken(Principal principal,
            @RequestHeader(value = "Host") String host,
            @RequestHeader(value = "User-Agent") String agent) throws Exception {
        DefaultOAuth2AccessToken accessToken = null;
        try {
            accessToken = tokenHandler.generateAccessToken(principal, host, agent, tokenServices);
            OAuth2Authentication authentication = tokenStore.readAuthentication(accessToken);
            tokenStore.removeAccessToken(accessToken);
            tokenStore.storeAccessToken(accessToken, authentication);
        } catch(DuplicateKeyException e) {
            List<OAuth2AccessToken> tokens = new ArrayList();
            tokens.addAll(tokenStore.findTokensByUserName(principal.getName()));
            accessToken = (DefaultOAuth2AccessToken)tokens.get(0);
        }
        APIOAuth2AccessToken result = new APIOAuth2AccessToken(accessToken);
        logger.debug("Created Token for {} is {}", principal.getName(), accessToken.getValue());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/refreshaccesstoken",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE},
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity refreshToken(@RequestHeader(value = "Authorization") String refreshAccessToken) throws Exception {
        refreshAccessToken = tokenHandler.parseAccessToken(refreshAccessToken);
        OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refreshAccessToken);
        OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
        OAuth2AccessToken oldAccessToken = tokenStore.getAccessToken(authentication);
        APIOAuth2AccessToken result = new APIOAuth2AccessToken(oldAccessToken);

        if (oldAccessToken.isExpired()) {
            DefaultOAuth2AccessToken accessToken;
            try {
                accessToken = tokenHandler.refreshAccessToken(refreshAccessToken, tokenServices);
                tokenStore.removeAccessToken(accessToken);
                accessToken.setAdditionalInformation(oldAccessToken.getAdditionalInformation());
                tokenStore.storeAccessToken(accessToken, authentication);
            } catch(DuplicateKeyException e) {
                List<OAuth2AccessToken> tokens = new ArrayList();
                tokens.addAll(tokenStore.findTokensByUserName(oldAccessToken.getAdditionalInformation().get("userId").toString()));
                accessToken = (DefaultOAuth2AccessToken)tokens.get(0);
            }
            logger.debug("Refreshed Token for refreshToken {} is {}", refreshAccessToken, accessToken.getValue());
            result = new APIOAuth2AccessToken(accessToken);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
