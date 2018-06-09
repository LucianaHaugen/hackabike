package wirelesscar.hackabike.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class Process implements RequestHandler<BikeInput, List<Byte>> {

  public List<Byte> handleRequest(BikeInput rawData, Context context) {
    System.out.println(rawData.toString());
    boolean hasData = true;
    rawData.getType();
    String message = rawData.getMsg();
    List<Byte> listOfCommands = new ArrayList();
    int position = 0;
    while (hasData) {
      if (message.charAt(position) == '\\') {
        int value = Integer.parseInt(message.substring(position + 2, position + 4), 16);
        listOfCommands.add((byte) value);
        position += 4;
      } else {
        listOfCommands.add((byte) message.charAt(position));
        position++;
      }
      if (message.length() == position) {
        hasData = false;
      }
    }
    System.out.println(listOfCommands);
    return listOfCommands;
  }
}
