package wirelesscar.hackabike.Util;

import static wirelesscar.hackabike.Util.DynamoDbUtil.mapper;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

import wirelesscar.hackabike.persistance.Bike;

public class BikeUtil {

  public static void addBikeCause(Bike bike, String cause) {
    if (bike.getCauses() != null) {
      bike.getCauses().put(cause, 0);
    } else {
      Map<String, Integer> causes = new HashMap<>();
      causes.put(cause, 0);
      bike.setCauses(causes);
    }
    bike.setActiveCause(cause);

    mapper.save(bike);
  }

  public static Bike getBike(Integer bikeId) {
    Bike bike = new Bike();
    bike.setBikeId(bikeId);
    DynamoDBQueryExpression<Bike> query = new DynamoDBQueryExpression<Bike>().withHashKeyValues(bike);
    PaginatedQueryList<Bike> query1 = mapper.query(Bike.class, query);
    if (query1.size() > 0) {
      return query1.get(0);
    }
    return null;
  }

  public static void setActiveCause(int id, String cause) {
    Bike bike = new Bike();
    bike.setBikeId(id);
    bike.setActiveCause(cause);

    mapper.save(bike);
  }

  public static void save(Bike bike) {
    mapper.save(bike);
  }
}
