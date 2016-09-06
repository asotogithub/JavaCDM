package trueffect.truconnect.api.oauth.encoding;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Rambert Rioja
 */
public class EncryptUtil {

    private static byte[] key = {
        0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53,
        0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79
    };

    public static String encryptAES(String strToEncrypt) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        String encryptedString = new String(
                Base64.encodeBase64(
                cipher.doFinal(strToEncrypt.getBytes())));
        return encryptedString;

    }
}
