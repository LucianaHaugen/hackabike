package wirelesscar.hackabike.lambda;

import static wirelesscar.hackabike.Util.BikeUtil.getBike;
import static wirelesscar.hackabike.Util.BikeUtil.save;

import java.util.HashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import wirelesscar.hackabike.persistance.Bike;

public class ProcessPosition implements RequestHandler<PositionInput, PositionInput> {

  public PositionInput handleRequest(PositionInput newPosition, Context context) {
    int bikeNr = 123; // TODO //
    if (newPosition.getBikeId() != null) {
      bikeNr = newPosition.getBikeId();
    }

    Bike theBike = getBike(bikeNr);
    Double oldDistanceTravelled = 0.0;
    if (theBike == null) {
      theBike = new Bike();
      theBike.setBikeId(bikeNr);
      theBike.setDistanceTravelled(0.0);
      theBike.setActiveCause("Reduce traffic");
      theBike.setCauses(new HashMap<>());
      theBike.getCauses().put(theBike.getActiveCause(), 0);
    } else {
      // TODO // only process updates which have a higher timestamp than the current last-seen-timestamp?

      // existing bike -- calculate a delta from lat and long differences of last observation
      Double lat1 = theBike.getLastSeenLatitude();
      Double long1 = theBike.getLastSeenLongitude();
      Double lat2 = newPosition.getLatitude();
      Double long2 = newPosition.getLongitude();
      if (lat1 != null && long1 != null && lat2 != null && long2 != null) {
        double distance = distance(lat1, lat2, long1, long2, 0, 0);
        if (theBike.getDistanceTravelled() == null) {
          theBike.setDistanceTravelled(0.0);
        }

        theBike.setDistanceTravelled(theBike.getDistanceTravelled() + distance);
        System.out.println(String.format("New distance travelled: %f", theBike.getDistanceTravelled()));

        if (theBike.getActiveCause() != null && !theBike.getActiveCause().isEmpty()) {
          // add on the distance travelled to whichever is the current cause for the bike
          if (theBike.getCauses() == null) {
            theBike.setCauses(new HashMap<String, Integer>());
          }

          if (theBike.getCauses().get(theBike.getActiveCause()) == null) {
            theBike.getCauses().put(theBike.getActiveCause(), 0);
          }

          Integer currentCauseScore = theBike.getCauses().get(theBike.getActiveCause());

          if (currentCauseScore == null) {
            currentCauseScore = (int) distance;
            theBike.getCauses().put(theBike.getActiveCause(), currentCauseScore);
          } else {
            currentCauseScore = currentCauseScore + (int) distance;
            theBike.getCauses().put(theBike.getActiveCause(), currentCauseScore);
          }

        }
      }
    }
    theBike.setLastSeenLatitude(newPosition.getLatitude());
    theBike.setLastSeenLongitude(newPosition.getLongitude());
    theBike.setLastSeenTimestamp(newPosition.getTimestamp());

    save(theBike);

    return newPosition;
  }

  /**
   * Calculate distance between two points in latitude and longitude taking into account height difference. If you are not interested in height difference pass
   * 0.0. Uses Haversine method as its base.
   *
   * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters el2 End altitude in meters
   * 
   * @returns Distance in Meters
   */
  public static double distance(double lat1, double lat2, double lon1,
      double lon2, double el1, double el2) {

    final int R = 6371; // Radius of the earth

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c * 1000; // convert to meters

    double height = el1 - el2;

    distance = Math.pow(distance, 2) + Math.pow(height, 2);

    return Math.sqrt(distance);
  }

}
