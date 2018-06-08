package wirelesscar.hackabike.persistance;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "bikes")
public class Bike {

  private Integer bikeId;

  private Map<String, Integer> causes;

  @DynamoDBHashKey(attributeName = "bikeId")
  public Integer getBikeId() {
    return bikeId;
  }

  public void setBikeId(Integer bikeId) {
    this.bikeId = bikeId;
  }

  @DynamoDBAttribute(attributeName = "causes")
  public Map<String, Integer> getCauses() {
    return causes;
  }

  public void setCauses(Map<String, Integer> causes) {
    this.causes = causes;
  }
}
