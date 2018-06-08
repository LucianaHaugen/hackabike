package wirelesscar.hackabike.lambda;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import wirelesscar.hackabike.persistance.Bike;

import java.util.ArrayList;
import java.util.List;

public class ProcessPosition implements RequestHandler<PositionInput, String> {
  private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
    .withRegion("us-east-1")
    .build();
  private static DynamoDBMapper mapper = new DynamoDBMapper(client);

  public String handleRequest(PositionInput newPosition, Context context) {
    int bikeNr = 123; // TODO //
    if (newPosition.getBikeId() != null) {
      bikeNr = newPosition.getBikeId();
    }

    List<Bike> theBikes = getBike(bikeNr);
    Bike theBike;
    Double oldDistanceTravelled = 0.0;
    if (theBikes.isEmpty()) {
      theBike = new Bike();
      theBike.setBikeId(bikeNr);
      theBike.setDistanceTravelled(0.0);
    } else {
      theBike = theBikes.get(0);
      // existing bike -- calculate a delta from lat and long differences of last observation
      Double lat1 = theBike.getLastSeenLatitude();
      Double long1 = theBike.getLastSeenLongitude();
      Double lat2 = newPosition.getLatitude();
      Double long2 = newPosition.getLongitude();
      if (lat1 != null && long1 != null && lat2 != null && long2 != null) {
        double distance = distance(lat1, lat2, long1, long2, 0, 0);
        oldDistanceTravelled = theBike.getDistanceTravelled();
        theBike.setDistanceTravelled(theBike.getDistanceTravelled() + distance);
      }
    }
    theBike.setLastSeenLatitude(newPosition.getLatitude());
    theBike.setLastSeenLongitude(newPosition.getLongitude());

    mapper.save(theBike);

    return "Old distance travelled: " + oldDistanceTravelled + " ; New distance travelled: " + theBike.getDistanceTravelled();
  }

  private List<Bike> getBike(Integer bikeId) {
    Bike bike = new Bike();
    bike.setBikeId(bikeId);
    DynamoDBQueryExpression<Bike> query = new DynamoDBQueryExpression<Bike>().withHashKeyValues(bike);
    return mapper.query(Bike.class, query);
  }


  /**
   * Calculate distance between two points in latitude and longitude taking
   * into account height difference. If you are not interested in height
   * difference pass 0.0. Uses Haversine method as its base.
   *
   * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
   * el2 End altitude in meters
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
