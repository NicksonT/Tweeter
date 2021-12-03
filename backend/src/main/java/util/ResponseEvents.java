package util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Map;

public class ResponseEvents {
    private static final Map<String, String> CorsHeaders = Map.of("Access-Control-Allow-Origin", "*", "Access-Control-Allow-Methods", "POST", "Access-Control-Allow-Headers", "Content-Type");

    public static APIGatewayProxyResponseEvent notFound() {
        return new APIGatewayProxyResponseEvent().withStatusCode(404).withHeaders(CorsHeaders);
    }

    public static APIGatewayProxyResponseEvent forbidden() {
        return new APIGatewayProxyResponseEvent().withStatusCode(403).withHeaders(CorsHeaders);
    }

    public static APIGatewayProxyResponseEvent ok() {
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(CorsHeaders);
    }

    public static APIGatewayProxyResponseEvent ok(String body) {
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(CorsHeaders).withBody(body);
    }

    public static APIGatewayProxyResponseEvent serverError() {
        return new APIGatewayProxyResponseEvent().withStatusCode(500).withHeaders(CorsHeaders);
    }
}
