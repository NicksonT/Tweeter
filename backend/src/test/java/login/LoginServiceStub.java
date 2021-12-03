package login;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import static util.ResponseEvents.ok;

public class LoginServiceStub extends LoginService {
    public LoginServiceStub() {
    }

    @Override
    public APIGatewayProxyResponseEvent login(String basicAuth) {
        return ok(String.format("{ \"message\": \"Successfully signed in\",\"jwt\":\"%s\"}", "JWToken"));
    }
}
