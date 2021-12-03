package login;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mocks.FakeContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginHandlerTest {

    @Test
    void return404WhenPathNotFound() {
        LoginHandler handler = new LoginHandler();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPath("/path-does-not-exist");
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, new FakeContext());
        assert (response.getStatusCode() == 404);
    }

    @Test
    void return403WhenAuthorizationHeaderIsNotInProperFormat() {
        LoginHandler handler = new LoginHandler();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPath("/login/").withHttpMethod("POST").withHeaders(Map.of("Authorization", "FakeAuth"));
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, new FakeContext());
        assertEquals(403, response.getStatusCode());
    }

    @Test
    void return403WhenAuthorizationHeaderIsWrongType() {
        LoginHandler handler = new LoginHandler();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPath("/login/").withHttpMethod("POST").withHeaders(Map.of("Authorization", "Bearer 3jk3ej3n"));
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, new FakeContext());
        assertEquals(403, response.getStatusCode());
    }

    @Test
    void return200WithMessage() throws JsonProcessingException {
        FakeContext context = new FakeContext();
        LoginHandler handler = new LoginHandler(new LoginServiceStub());
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withPath("/login/").withHttpMethod("POST").withHeaders(Map.of("Authorization", "Basic dXNlcjE6dXNlcjE="));
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> o = mapper.readValue(response.getBody(), new TypeReference<HashMap<String, String>>() {
        });
        assertEquals(200, response.getStatusCode());
        assertEquals(o.get("message"), "Successfully signed in");
        assertTrue(o.containsKey("jwt"));

    }

}