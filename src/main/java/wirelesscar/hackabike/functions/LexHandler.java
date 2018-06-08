package wirelesscar.hackabike.functions;

import static wirelesscar.hackabike.Util.BikeUtil.getBike;
import static wirelesscar.hackabike.Util.BikeUtil.setActiveCause;
import static wirelesscar.hackabike.Util.BikeUtil.updateBikeCauses;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

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

  public LexResponse oldhandleRequest(LexRequest input, Context context) {

    if (getBike(input.getBikeId()).getCauses().get(input.getCause()) != null) {
      updateBikeCauses(input.getBikeId(), input.getCause(), 0);
    }

    LexResponse lexResponse = new LexResponse();
    lexResponse.setCause(input.getCause());
    return lexResponse;
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
        updateBikeCauses(bikeId, cause, 0);
      } else {
        setActiveCause(bikeId, cause);
      }

    } catch (JSONException e) {
      System.out.println("Couldn't parse json");
    }
    output.write(response.getBytes());
  }

}
