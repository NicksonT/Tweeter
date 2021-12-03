package storage;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class StorageService {
    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    private final String TWEETS_DYNAMODB_TABLE_NAME = "tweets";
    private final Comparator<Map<String, AttributeValue>> timeComparator = Comparator.comparing(m -> m.get("posted_date").getS());
    private AmazonDynamoDB dynamoDb;

    public StorageService() {
        initDynamoDbClient();
    }

    public List<String> getHashAndSaltFromDatabaseFor(String username) throws Exception {
        String USER_DYNAMODB_TABLE_NAME = "users";
        GetItemResult getItemResult = dynamoDb.getItem(USER_DYNAMODB_TABLE_NAME, Map.of("username", new AttributeValue(username)));
        if (getItemResult.getItem() == null) {
            logger.warn(String.format("User %s does not exist", username));
            throw new Exception("User does not exist");
        }
        return List.of(getItemResult.getItem().get("hash").getS(), getItemResult.getItem().get("salt").getS());

    }

    public List<Map<String, AttributeValue>> getAllTweets() {
        ScanResult scanResult = dynamoDb.scan(TWEETS_DYNAMODB_TABLE_NAME, List.of("username", "tweet_body", "posted_date"));
        List<Map<String, AttributeValue>> listOfItems = scanResult.getItems();
        listOfItems.sort(Collections.reverseOrder(timeComparator));
        return listOfItems;
    }

    private void initDynamoDbClient() {
        dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();
    }

    public void newTweet(String currentTime, String user, String body) {
        dynamoDb.putItem(TWEETS_DYNAMODB_TABLE_NAME, Map.of("tweet_id", new AttributeValue(String.valueOf(UUID.randomUUID())), "username", new AttributeValue(user), "tweet_body", new AttributeValue(body), "posted_date", new AttributeValue(currentTime)));
    }

}
