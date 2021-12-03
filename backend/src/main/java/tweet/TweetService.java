package tweet;

import auth.AuthService;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.StorageService;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.SECONDS;
import static util.ResponseEvents.forbidden;
import static util.ResponseEvents.ok;


public class TweetService {
    private static final Logger logger = LoggerFactory.getLogger(TweetService.class);
    private final String authorizationHeader;

    public TweetService(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }


    public APIGatewayProxyResponseEvent postTweet(String body) {
        try {
            String userName = AuthService.getUser(authorizationHeader);
            new StorageService().newTweet(Instant.now().truncatedTo(SECONDS).toString(), userName, body);
            return ok();
        } catch (JWTVerificationException e) {
            return forbidden();
        }
    }

    public APIGatewayProxyResponseEvent getTweets() throws JsonProcessingException {
        try {
            AuthService.verifyJWT(authorizationHeader);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            List<Map<String, AttributeValue>> tweets = new StorageService().getAllTweets();
            String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(tweets);
            return ok(jsonResult);
        } catch (JWTVerificationException e) {
            return forbidden();
        }
    }

}
