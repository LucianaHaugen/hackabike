package wirelesscar.hackabike.lambda;

public class InfoInput {
  Integer bikeId;

  String type;

  String msg;

  Long timestamp;

  public Integer getBikeId() {
    return bikeId;
  }

  public void setBikeId(Integer bikeId) {
    this.bikeId = bikeId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

}
