package wirelesscar.hackabike.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

import wirelesscar.hackabike.Util.CauseUtil;
import wirelesscar.hackabike.Util.DynamoDbUtil;
import wirelesscar.hackabike.persistance.Bike;
import wirelesscar.hackabike.persistance.Cause;

public class DynamoDbEventHandler implements RequestHandler<DynamodbEvent, String> {

  @Override
  public String handleRequest(DynamodbEvent input, Context context) {
    for (DynamodbEvent.DynamodbStreamRecord dynamodbStreamRecord : input.getRecords()) {
      handleRecord(dynamodbStreamRecord);
    }

    return "";
  }

  private void handleRecord(DynamodbEvent.DynamodbStreamRecord dynamodbStreamRecord) {
    Bike oldBike = DynamoDbUtil.mapper.marshallIntoObject(Bike.class, dynamodbStreamRecord.getDynamodb().getOldImage());
    Bike newBike = DynamoDbUtil.mapper.marshallIntoObject(Bike.class, dynamodbStreamRecord.getDynamodb().getNewImage());

    if (oldBike != null && newBike != null) {
      if (oldBike.getActiveCause().equals(newBike.getActiveCause())) {
        double distance = newBike.getDistanceTravelled() - oldBike.getDistanceTravelled();
        if (distance > 0) {
          System.out.println(String.format("DIstance travelled: %f", distance));
          Cause cause = CauseUtil.getCause(newBike.getActiveCause());
          cause.setActualDistance(cause.getActualDistance() + distance);
          CauseUtil.save(cause);
        }
      }
    }
  }
}
