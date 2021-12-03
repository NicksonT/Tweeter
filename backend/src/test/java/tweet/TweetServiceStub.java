package tweet;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import static util.ResponseEvents.ok;

public class TweetServiceStub extends TweetService {
    public TweetServiceStub(String authorizationHeader) {
        super(authorizationHeader);
    }

    @Override
    public APIGatewayProxyResponseEvent getTweets() {
        return ok();
    }

    @Override
    public APIGatewayProxyResponseEvent postTweet(String body) {
        return ok();
    }
}
