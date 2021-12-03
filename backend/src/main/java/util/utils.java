package util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public class utils {

    public static String getAuthHeader(APIGatewayProxyRequestEvent event, String type) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        String[] authHeaderSplit = event.getHeaders().get("Authorization").split(" ");
        if (!authHeaderSplit[0].equals(type))
            throw new IllegalArgumentException();
        return authHeaderSplit[1];
    }

    public static boolean route(APIGatewayProxyRequestEvent event, String path, String method) {
        return (event.getPath().equals(path) && event.getHttpMethod().equals(method));
    }
}
