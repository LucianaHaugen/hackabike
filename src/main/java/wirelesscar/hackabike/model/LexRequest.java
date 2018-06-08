package wirelesscar.hackabike.model;

public class LexRequest {

  Integer bikeId;
  String cause;

  public LexRequest() {}

  public Integer getBikeId() {
    return bikeId;
  }

  public void setBikeId(Integer bikeId) {
    this.bikeId = bikeId;
  }

  public String getCause() {
    return cause;
  }

  public void setCause(String cause) {
    this.cause = cause;
  }

}
