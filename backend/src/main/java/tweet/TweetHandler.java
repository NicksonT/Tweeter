package tweet;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.ResponseEvents.*;
import static util.utils.getAuthHeader;
import static util.utils.route;

public class TweetHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(TweetHandler.class);
    private TweetService service;
    private APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent;

    public TweetHandler() {

    }

    public TweetHandler(TweetService service) {
        this.service = service;
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        try {
            String authHeader = getAuthHeader(apiGatewayProxyRequestEvent, "Bearer");
            logger.info(authHeader);
            if (service == null)
                service = new TweetService(authHeader);

            if (route(apiGatewayProxyRequestEvent, "/tweet/", "POST"))
                return service.postTweet(apiGatewayProxyRequestEvent.getBody());

            else if (route(apiGatewayProxyRequestEvent, "/tweet/", "GET"))
                return service.getTweets();

            else
                return notFound();

        } catch (JsonProcessingException e) {
            return serverError();
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            logger.warn("Error extracting authorization header");
            return forbidden();
        }
    }
}
