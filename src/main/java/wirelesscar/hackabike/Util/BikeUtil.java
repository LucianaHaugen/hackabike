package wirelesscar.hackabike.Util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

import wirelesscar.hackabike.persistance.Bike;

public class BikeUtil {
  private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
      .withRegion("us-east-1")
      .build();
  private static DynamoDBMapper mapper = new DynamoDBMapper(client);

  public static void addBikeCause(Integer id, String cause) {
    Bike bike = getBike(id);
    bike.getCauses().put(cause, 0);
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
