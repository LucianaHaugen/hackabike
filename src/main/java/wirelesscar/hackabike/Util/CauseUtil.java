package wirelesscar.hackabike.Util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

import wirelesscar.hackabike.persistance.Cause;

public class CauseUtil {
  private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
      .withRegion("us-east-1")
      .build();
  private static DynamoDBMapper mapper = new DynamoDBMapper(client);

  public static Cause getCause(String causeId) {
    System.out.println("Querying for: " + causeId);
    Cause cause = new Cause();
    cause.setCauseId(causeId);
    DynamoDBQueryExpression<Cause> query = new DynamoDBQueryExpression<Cause>().withHashKeyValues(cause);
    PaginatedQueryList<Cause> query1 = mapper.query(Cause.class, query);
    if (query1.size() > 0) {
      System.out.println("Found cause in table");
      return query1.get(0);
    }
    System.out.println("No result found");
    return null;
  }

  public static void addCause() {
    Cause causeHealth = new Cause();
    causeHealth.setCauseId("Get healthy!");
    causeHealth.setSponsor("WirelessCar");
    causeHealth.setGoalDistance(100000L);
    causeHealth.setActualDistance(0.0);
    causeHealth.setOrganization("The Swedish Heart-Lung Foundation");

    Cause causeTrafic = new Cause();
    causeTrafic.setCauseId("Reduce traffic!");
    causeTrafic.setSponsor("Cybercom Group");
    causeTrafic.setGoalDistance(120000L);
    causeTrafic.setActualDistance(0.0);
    causeTrafic.setOrganization("Swedish Society for Nature Conservation");

    Cause causeEnviroment = new Cause();
    causeEnviroment.setCauseId("Save the environment!");
    causeEnviroment.setSponsor("Bike Europe");
    causeEnviroment.setGoalDistance(150000L);
    causeEnviroment.setActualDistance(0.0);
    causeEnviroment.setOrganization("Greenpeace");

    mapper.save(causeHealth);
    mapper.save(causeTrafic);
    mapper.save(causeEnviroment);
  }

  public void updateCause(String causeId, Double actualDistance) {
    Cause cause = getCause(causeId);
    cause.setActualDistance(actualDistance);
    mapper.save(cause);
  }

  public static void save(Cause cause) {
    mapper.save(cause);
  }
}
