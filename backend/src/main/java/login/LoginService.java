package login;

import auth.AuthService;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.StorageService;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

import static com.amazonaws.util.BinaryUtils.fromHex;
import static util.ResponseEvents.ok;

public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    public LoginService() {
    }

    public APIGatewayProxyResponseEvent login(String basicAuth) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(basicAuth);
            String[] usernameAndPass = new String(decodedBytes).split(":");
            verifyPassword(usernameAndPass);
            String JWToken = AuthService.generateJWT(usernameAndPass[0]);
            return ok(String.format("{ \"message\": \"Successfully signed in\",\"jwt\":\"%s\"}", JWToken));
        } catch (JWTCreationException e) {
            logger.warn("Unable to generate JWT token");
            logger.warn(String.valueOf(e));
            return new APIGatewayProxyResponseEvent().withStatusCode(500);
        } catch (Exception e) {
            logger.warn(e.toString());
            return new APIGatewayProxyResponseEvent().withStatusCode(403).withBody(String.format("{ \"message\": \"%s\"}", e.getMessage()));
        }
    }


    private void verifyPassword(String[] decodedBytes) throws Exception {
        String username = decodedBytes[0];
        String password = decodedBytes[1];
        List<String> hashSalt = new StorageService().getHashAndSaltFromDatabaseFor(username);
        if (!validatePassword(password, hashSalt.get(1), hashSalt.get(0))) {
            logger.warn(String.format("Password doesn't match for %s", username));
            throw new Exception("Password doesn't match");
        }
    }

    private boolean validatePassword(String originalPassword, String saltHex, String hashHex)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = fromHex(saltHex);
        byte[] hash = fromHex(hashHex);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(),
                salt, 65536, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
}
