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

import wirelesscar.hackabike.model.LexRequest;
import wirelesscar.hackabike.model.LexResponse;
import wirelesscar.hackabike.persistance.Bike;

public class LexHandler implements RequestHandler<LexRequest, LexResponse> {
  private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
      .withRegion("us-east-1")
      .build();
  private static DynamoDBMapper mapper = new DynamoDBMapper(client);

  public LexResponse handleRequest(LexRequest input, Context context) {

    // Very ugly but it should not be possible to add a cause if it already exists
    if (getBike(input.getBikeId()).get(0).getCauses().get(input.getCause()) != null) {
      updateBike(input.getBikeId(), input.getCause(), 0);
    }

    LexResponse lexResponse = new LexResponse();
    lexResponse.setCause(input.getCause());
    return lexResponse;
  }

  private void updateBike(Integer id, String cause, Integer value) {
    Bike bike = new Bike();
    bike.setBikeId(id);
    Map<String, Integer> causes = new HashMap<>();
    causes.put(cause, value);
    bike.setCauses(causes);

    mapper.save(bike);
  }

  private List<Bike> getBike(Integer bikeId) {
    Bike bike = new Bike();
    bike.setBikeId(bikeId);
    DynamoDBQueryExpression<Bike> query = new DynamoDBQueryExpression<Bike>().withHashKeyValues(bike);
    return mapper.query(Bike.class, query);
  }
}
