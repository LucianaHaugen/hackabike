package wirelesscar.hackabike.Util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDbUtil {
  public static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
      .withRegion("us-east-1")
      .build();
  public static final DynamoDBMapper mapper = new DynamoDBMapper(client);

}
