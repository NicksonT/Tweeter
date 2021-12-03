package tweet;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import mocks.FakeContext;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TweetHandlerTest {
    @Test
    void return404WhenPathNotFound() {
        TweetHandler handler = new TweetHandler();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPath("/path-does-not-exist").withHeaders(Map.of("Authorization", "Bearer test"));
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, new FakeContext());
        assert (response.getStatusCode() == 404);
    }

    @Test
    void return403WhenAuthorizationHeaderIsNotInProperFormat() {
        TweetHandler handler = new TweetHandler();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPath("/tweet/").withHttpMethod("POST").withHeaders(Map.of("Authorization", "FakeAuth"));
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, new FakeContext());
        assertEquals(403, response.getStatusCode());
    }

    @Test
    void return403WhenAuthorizationHeaderIsWrongType() {
        TweetHandler handler = new TweetHandler();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPath("/tweet/").withHttpMethod("POST").withHeaders(Map.of("Authorization", "Bearer 3jk3ej3n"));
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, new FakeContext());
        assertEquals(403, response.getStatusCode());
    }

    @Test
    void return200() {
        FakeContext context = new FakeContext();
        TweetHandler handler = new TweetHandler(new TweetServiceStub(""));
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPath("/tweet/").withHttpMethod("POST").withHeaders(Map.of("Authorization", "Bearer dXNlcjE6dXNlcjE="));
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
        assertEquals(200, response.getStatusCode());
    }
}
