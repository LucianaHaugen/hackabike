package wirelesscar.hackabike.lambda;

public class PositionInput {
  Integer bikeId;

  Double latitude;

  Double longitude;

  Long timestamp;

  public Double getLatitude() { return latitude; }

  public Double getLongitude() { return longitude; }

  public void setLatitude(Double someLatitude) { latitude = someLatitude; }

  public void setLongitude(Double someLongitude) { longitude = someLongitude; }

  public void setBikeId(Integer bikeId) { this.bikeId = bikeId; }

  public Integer getBikeId() { return bikeId; }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestampMillis) {
    this.timestamp = timestampMillis;
  }

}
