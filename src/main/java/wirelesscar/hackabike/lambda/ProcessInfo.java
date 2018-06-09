package wirelesscar.hackabike.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import wirelesscar.hackabike.persistance.Bike;

import java.util.HashMap;

import static wirelesscar.hackabike.Util.BikeUtil.getBike;
import static wirelesscar.hackabike.Util.BikeUtil.save;

public class ProcessInfo implements RequestHandler<InfoInput, InfoInput> {

  public InfoInput handleRequest(InfoInput newInfo, Context context) {
    int bikeNr = 123; // TODO //
    if (newInfo.getBikeId() != null) {
      bikeNr = newInfo.getBikeId();
    }

    Bike theBike = getBike(bikeNr);
    if (newInfo.getMsg() != null) {
      Integer temp = transformInfoStringToTemperature(newInfo.getMsg());
      theBike.setLastSeenTemperature(temp);
    }
    save(theBike);

    return newInfo;
  }

  Integer transformInfoStringToTemperature(String input) {
    if (input.startsWith("\\x")) {
      return Integer.parseInt(input.substring(2, 4));
    } else {
      return (int) input.getBytes()[0];
    }
  }

}
