package login;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.ResponseEvents.forbidden;
import static util.ResponseEvents.notFound;
import static util.utils.getAuthHeader;
import static util.utils.route;

public class LoginHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final LoginService service;
    private APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent;

    public LoginHandler() {
        service = new LoginService();
    }

    public LoginHandler(LoginService service) {
        this.service = service;
    }


    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        if (route(apiGatewayProxyRequestEvent, "/login/", "POST")) {
            try {
                return service.login(getAuthHeader(apiGatewayProxyRequestEvent, "Basic"));
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                logger.warn("Error extracting authorization header");
                return forbidden();
            }
        } else
            return notFound();
    }


}

