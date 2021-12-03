package auth;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class AuthService {

    public static String generateJWT(String username) throws JWTCreationException {
        String secret = getSecret("jwt/key");
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("tweeter")
                .withSubject(username)
                .sign(algorithm);
    }

    private static String getSecret(String secretName) {

        String region = "eu-west-1";

        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getSecretValueResult.getSecretString();
    }

    public static String getUser(String token) throws JWTVerificationException {
        DecodedJWT validJWT = verifyJWT(token);

        return validJWT.getSubject();
    }

    public static DecodedJWT verifyJWT(String token) throws JWTVerificationException {

        String secret = getSecret("jwt/key");
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("tweeter")
                .build(); //Reusable verifier instance
        return verifier.verify(token);
    }
}
