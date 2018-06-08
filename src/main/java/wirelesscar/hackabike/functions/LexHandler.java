package wirelesscar.hackabike.functions;

import static wirelesscar.hackabike.Util.BikeUtil.addBikeCause;
import static wirelesscar.hackabike.Util.BikeUtil.getBike;
import static wirelesscar.hackabike.Util.BikeUtil.save;
import static wirelesscar.hackabike.Util.BikeUtil.setActiveCause;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.IOUtils;

import wirelesscar.hackabike.persistance.Bike;

public class LexHandler implements RequestStreamHandler {

  @Override
  public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    String jsonString = IOUtils.toString(input);
    System.out.println(String.format("Got: %s", jsonString));
    try {
      JSONObject json = new JSONObject(jsonString);
      String intent = json.getJSONObject("currentIntent").getString("name");
      switch (intent) {
        case "SelectCause":
          handleSelectCause(json, output);
          break;
        case "AskForCauseScore":
          handleAskForScore(json, output);
          break;
        default:
          break;
      }

    } catch (JSONException e) {
      System.out.println("Couldn't parse json");
    }
  }

  private static final String responseSelectCause = "{  \n" +
      "    \"dialogAction\": {\n" +
      "        \"type\": \"Close\",\n" +
      "        \"fulfillmentState\": \"Fulfilled\",\n" +
      "        \"message\": {\n" +
      "            \"contentType\": \"PlainText\",\n" +
      "            \"content\": \"You are now biking with a cause!\"\n" +
      "        }\n" +
      "    }\n" +
      "};";

  private void handleSelectCause(JSONObject json, OutputStream output) throws IOException {
    String cause = json.getJSONObject("currentIntent")
        .getJSONObject("slots")
        .getString("Cause");

    int bikeId = Integer.valueOf(json.getJSONObject("currentIntent")
        .getJSONObject("slots")
        .getString("BikeId"));

    Bike bike = getBike(bikeId);
    if (bike == null) {
      Bike newBike = new Bike();
      newBike.setBikeId(bikeId);
      Map<String, Integer> causes = new HashMap<>();
      causes.put(cause, 0);
      newBike.setCauses(causes);
      newBike.setActiveCause(cause);
      save(newBike);

    } else if (bike.getCauses().get(cause) == null) {
      addBikeCause(bikeId, cause);
    } else {
      setActiveCause(bikeId, cause);
    }
    output.write(responseSelectCause.getBytes());
  }

  private static final String responseAskForScore = "{  \n" +
      "    \"dialogAction\": {\n" +
      "        \"type\": \"Close\",\n" +
      "        \"fulfillmentState\": \"Fulfilled\",\n" +
      "        \"message\": {\n" +
      "            \"contentType\": \"PlainText\",\n" +
      "            \"content\": \"MESSAGE\"\n" +
      "        }\n" +
      "    }\n" +
      "};";

  private void handleAskForScore(JSONObject json, OutputStream output) throws IOException {
    int bikeId = Integer.valueOf(json.getJSONObject("currentIntent")
        .getJSONObject("slots")
        .getString("BikeId"));

    Bike bike = getBike(bikeId);
    Integer currentScore = 0;
    if (bike.getCauses() != null && bike.getActiveCause() != null && bike.getCauses().get(bike.getActiveCause()) != null) {
      currentScore = bike.getCauses().get(bike.getActiveCause());
      String response = "Bike " + bikeId + " is running for the cause of " + bike.getActiveCause().replace("!", "") + " and it has collected " +
          currentScore + " meters!";
      output.write(responseAskForScore.replace("MESSAGE", response).getBytes());
    } else {
      output.write(responseAskForScore.replace("MESSAGE", "Sorry, I don't know how it is going.").getBytes());
    }
  }

}
