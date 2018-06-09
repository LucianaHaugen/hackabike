package wirelesscar.hackabike.persistance;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "causes")
public class Cause {

  private String causeId;
  private String sponsor;
  private Long goalDistance;
  private Double actualDistance;
  private String organization;

  @DynamoDBHashKey(attributeName = "causeId")
  public String getCauseId() {
    return causeId;
  }

  public void setCauseId(String causeId) {
    this.causeId = causeId;
  }

  public String getSponsor() {
    return sponsor;
  }

  public void setSponsor(String sponsor) {
    this.sponsor = sponsor;
  }

  public Long getGoalDistance() {
    return goalDistance;
  }

  public void setGoalDistance(Long goalDistance) {
    this.goalDistance = goalDistance;
  }

  public Double getActualDistance() {
    return actualDistance;
  }

  public void setActualDistance(Double actualDistance) {
    this.actualDistance = actualDistance;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }
}
