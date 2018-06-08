package wirelesscar.hackabike.lambda.test;

import org.junit.Test;

import wirelesscar.hackabike.lambda.BikeInput;
import wirelesscar.hackabike.lambda.Process;

public class ProcessTest {

  @Test
  public void runInput() {
    Process process = new Process();
    BikeInput bikeInput = new BikeInput();
    bikeInput.setMsg("\\x00\\x00\\x01\\x00\\x00 bb00\\x00\\x00 \\x00\\x00\\xff\\xff\\xfe\\x00\\x00 \\x00");
    bikeInput.setType("motor");

    process.handleRequest(bikeInput, null);
  }
}
