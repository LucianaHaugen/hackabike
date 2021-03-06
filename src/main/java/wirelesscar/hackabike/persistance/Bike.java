package wirelesscar.hackabike.persistance;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "bikes")
public class Bike {

  private Integer bikeId;

  private Map<String, Integer> causes;

  private String activeCause;

  private Double lastSeenLatitude;
  private Double lastSeenLongitude;

  private Long lastSeenTimestamp;

  private Double distanceTravelled;

  private Integer lastSeenTemperature;

  private Boolean lastLightsOn;

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

  @DynamoDBAttribute(attributeName = "activeCause")
  public String getActiveCause() {
    return activeCause;
  }

  public void setActiveCause(String activeCause) {
    this.activeCause = activeCause;
  }

  public Double getLastSeenLatitude() {
    return lastSeenLatitude;
  }

  public void setLastSeenLatitude(Double lastSeenLatitude) {
    this.lastSeenLatitude = lastSeenLatitude;
  }

  public Double getLastSeenLongitude() {
    return lastSeenLongitude;
  }

  public void setLastSeenLongitude(Double lastSeenLongitude) {
    this.lastSeenLongitude = lastSeenLongitude;
  }

  public Double getDistanceTravelled() {
    return distanceTravelled;
  }

  public void setDistanceTravelled(Double distanceTravelled) {
    this.distanceTravelled = distanceTravelled;
  }

  public Long getLastSeenTimestamp() {
    return lastSeenTimestamp;
  }

  public void setLastSeenTimestamp(Long lastSeenTimestamp) {
    this.lastSeenTimestamp = lastSeenTimestamp;
  }

  public Integer getLastSeenTemperature() {
    return lastSeenTemperature;
  }

  public void setLastSeenTemperature(Integer lastSeenTemperature) {
    this.lastSeenTemperature = lastSeenTemperature;
  }

  public Boolean getLastLightsOn() {
    return lastLightsOn;
  }

  public void setLastLightsOn(Boolean lastLightsOn) {
    this.lastLightsOn = lastLightsOn;
  }

  @Override
  public String toString() {
    return "Bike{" +
            "bikeId=" + bikeId +
            ", causes=" + causes +
            ", activeCause='" + activeCause + '\'' +
            ", lastSeenLatitude=" + lastSeenLatitude +
            ", lastSeenLongitude=" + lastSeenLongitude +
            ", lastSeenTimestamp=" + lastSeenTimestamp +
            ", distanceTravelled=" + distanceTravelled +
            ", lastSeenTemperature=" + lastSeenTemperature +
            ", lastLightsOn=" + lastLightsOn +
            '}';
  }
}
