package wirelesscar.hackabike.lambda;

public class BikeInput {

  String msg;
  String type;

  public BikeInput() {}

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "BikeInput{" +
        "msg='" + msg + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}
