package wirelesscar.hackabike.functions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.IOUtils;

import wirelesscar.hackabike.model.LexRequest;
import wirelesscar.hackabike.model.LexResponse;
import wirelesscar.hackabike.persistance.Bike;

public class LexHandler implements RequestStreamHandler {
  private static final String response = "{  \n" +
      "    \"dialogAction\": {\n" +
      "        \"type\": \"Close\",\n" +
      "        \"fulfillmentState\": \"Fulfilled\",\n" +
      "        \"message\": {\n" +
      "            \"contentType\": \"PlainText\",\n" +
      "            \"content\": \"You are now biking with a cause!\"\n" +
      "        }\n" +
      "    }\n" +
      "};";

  private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
      .withRegion("us-east-1")
      .build();
  private static DynamoDBMapper mapper = new DynamoDBMapper(client);

  public LexResponse oldhandleRequest(LexRequest input, Context context) {

    if (getBike(input.getBikeId()).getCauses().get(input.getCause()) != null) {
      updateBike(input.getBikeId(), input.getCause(), 0);
    }

    LexResponse lexResponse = new LexResponse();
    lexResponse.setCause(input.getCause());
    return lexResponse;
  }

  private void updateBike(Integer id, String cause, Integer value) {
    Bike bike = getBike(id);
    bike.getCauses().put(cause, value);
    bike.setActiveCause(cause);

    mapper.save(bike);
  }

  private Bike getBike(Integer bikeId) {
    Bike bike = new Bike();
    bike.setBikeId(bikeId);
    DynamoDBQueryExpression<Bike> query = new DynamoDBQueryExpression<Bike>().withHashKeyValues(bike);
    PaginatedQueryList<Bike> query1 = mapper.query(Bike.class, query);
    if (query1.size() > 0) {
      return query1.get(0);
    }
    return null;
  }

  @Override
  public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    String jsonString = IOUtils.toString(input);
    System.out.println(String.format("Got: %s", jsonString));
    try {
      JSONObject json = new JSONObject(jsonString);
      String cause = json.getJSONObject("currentIntent")
          .getJSONObject("slots")
          .getString("Cause");

      int bikeId = Integer.valueOf(json.getJSONObject("currentIntent")
          .getJSONObject("slots")
          .getString("BikeId"));

      Bike bike = getBike(bikeId);
      if (bike == null || bike.getCauses().get(cause) == null) {
        updateBike(bikeId, cause, 0);
      } else {
        setActiveCause(bikeId, cause);
      }

    } catch (JSONException e) {
      System.out.println("Couldn't parse json");
    }
    output.write(response.getBytes());
  }

  private void setActiveCause(int id, String cause) {
    Bike bike = new Bike();
    bike.setBikeId(id);
    bike.setActiveCause(cause);

    mapper.save(bike);
  }
}
