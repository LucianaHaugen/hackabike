package wirelesscar.hackabike.functions;

import static wirelesscar.hackabike.Util.BikeUtil.addBikeCause;
import static wirelesscar.hackabike.Util.BikeUtil.getBike;
import static wirelesscar.hackabike.Util.BikeUtil.save;
import static wirelesscar.hackabike.Util.BikeUtil.setActiveCause;
import static wirelesscar.hackabike.Util.CauseUtil.getCause;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.IOUtils;

import wirelesscar.hackabike.persistance.Bike;
import wirelesscar.hackabike.persistance.Cause;

public class LexHandler implements RequestStreamHandler {

  @Override
  public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    String jsonString = IOUtils.toString(input);
    System.out.println(String.format("Got: %s", jsonString));
    try {
      JSONObject json = new JSONObject(jsonString);
      String intent = json.getJSONObject("currentIntent").getString("name");
      System.out.println("Active intent: " + intent);
      switch (intent) {
        case "SelectCause":
          handleSelectCause(json, output);
          break;
        case "AskForCauseScore":
          handleAskForScore(json, output);
          break;
        case "AskAboutCause":
          handleAskAboutCause(json, output);
          break;
        case "GetBatteryTemperature":
          handleGetBatteryTemperature(json, output);
          break;
        case "FindMyBike":
          handleFindMyBike(json, output);
          break;
        default:
          break;
      }

    } catch (JSONException e) {
      System.out.println("Couldn't parse json");
    }
  }

  private void handleFindMyBike(JSONObject json, OutputStream output) throws IOException {
    int bikeId = Integer.valueOf(json.getJSONObject("currentIntent")
        .getJSONObject("slots")
        .getString("BikeId"));

    Bike bike = getBike(bikeId);
    if (bike != null && bike.getLastSeenLatitude() != null && bike.getLastSeenLongitude() != null) {

      try {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet();
        get.setURI(
            URI.create(
                String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f", bike.getLastSeenLatitude(), bike.getLastSeenLongitude())));
        HttpResponse response = httpClient.execute(get);
        String responseBlob = IOUtils.toString(response.getEntity().getContent());
        String streetNumber = responseBlob.split("\"formatted_address\" : \"")[1].split("\"")[0];

        output.write(genericResponse.replace("MESSAGE", String.format("Your bike is at: %s", streetNumber))
            .getBytes());
      } catch (Exception e) {
        output.write(genericResponse.replace("MESSAGE", String.format("I could not find any information about your bike")).getBytes());
      }
    } else {
      output.write(genericResponse.replace("MESSAGE", String.format("I could not find any information about your bike")).getBytes());
    }
  }

  private static final String genericResponse = "{  \n" +
      "    \"dialogAction\": {\n" +
      "        \"type\": \"Close\",\n" +
      "        \"fulfillmentState\": \"Fulfilled\",\n" +
      "        \"message\": {\n" +
      "            \"contentType\": \"PlainText\",\n" +
      "            \"content\": \"MESSAGE\"\n" +
      "        }\n" +
      "    }\n" +
      "};";

  private void handleGetBatteryTemperature(JSONObject json, OutputStream output) throws IOException {
    int bikeId = Integer.valueOf(json.getJSONObject("currentIntent")
        .getJSONObject("slots")
        .getString("BikeId"));

    Bike bike = getBike(bikeId);
    if (bike != null && bike.getLastSeenTemperature() != null) {

      output.write(genericResponse.replace("MESSAGE", String.format("The temperature around your bike is: %d degrees celsius", bike.getLastSeenTemperature()))
          .getBytes());

    } else {
      output.write(genericResponse.replace("MESSAGE", String.format("I could not find any information about your bike")).getBytes());
    }
  }

  private void handleAskAboutCause(JSONObject json, OutputStream output) throws IOException {
    String causeId = json.getJSONObject("currentIntent")
        .getJSONObject("slots")
        .getString("causeId");

    Cause cause = getCause(causeId);
    if (cause != null) {
      String response = "Cause " + causeId.replace("!", "") +
          " is currently running for " + cause.getSponsor() +
          " gathering meters for " +
          cause.getOrganization() +
          ". They have so far reached the distance of: " + cause.getActualDistance() +
          " meters, of their Goal distance of " + cause.getGoalDistance() + ".";
      output.write(responseAskForScore.replace("MESSAGE", response).getBytes());
    } else {
      output.write(responseAskForScore.replace("MESSAGE", "Sorry, I don't know that cause.").getBytes());
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

    } else if (bike.getCauses() == null || bike.getCauses().get(cause) == null) {
      addBikeCause(bike, cause);
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
