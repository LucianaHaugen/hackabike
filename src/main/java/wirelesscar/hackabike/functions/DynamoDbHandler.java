package wirelesscar.hackabike.functions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import wirelesscar.hackabike.model.DynamoDbHandlerResponse;
import wirelesscar.hackabike.model.ProcessResponse;

public class DynamoDbHandler implements RequestHandler<ProcessResponse, DynamoDbHandlerResponse> {
  private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
      .withRegion("us-east-1")
      .build();
  private static DynamoDBMapper mapper = new DynamoDBMapper(client);

  public DynamoDbHandlerResponse handleRequest(ProcessResponse input, Context context) {
    return null;
  }

}
