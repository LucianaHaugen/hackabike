package wirelesscar.hackabike.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import wirelesscar.hackabike.model.DynamoDbHandlerResponse;
import wirelesscar.hackabike.model.ProcessResponse;
import wirelesscar.hackabike.persistance.Bike;

public class DynamoDbHandler implements RequestHandler<ProcessResponse, DynamoDbHandlerResponse> {
  private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
      .withRegion("us-east-1")
      .build();
  private static DynamoDBMapper mapper = new DynamoDBMapper(client);

  public DynamoDbHandlerResponse handleRequest(ProcessResponse input, Context context) {
    return null;
  }

  private void updateBike(String id, String cause, Integer value) {
    Bike bike = new Bike();
    bike.setBikeId(2);
    Map<String, Integer> causes = new HashMap<>();
    causes.put(cause, value);
    bike.setCauses(causes);

    mapper.save(bike);
  }

  private List<Bike> getBike(Integer bikeId) {
    Bike bike = new Bike();
    bike.setBikeId(1);
    DynamoDBQueryExpression<Bike> query = new DynamoDBQueryExpression<Bike>().withHashKeyValues(bike);
    return mapper.query(Bike.class, query);
  }
}
