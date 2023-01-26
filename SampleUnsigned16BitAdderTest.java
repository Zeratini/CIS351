import org.junit.Assert;
import org.junit.Test;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Sample test cases for an unsigned, 16-bit adder with a carry-in and overflow.
 * IMPORTANT:  These test cases do *not* thoroughly test the adder.  You need to
 * re-name this class and add more tests!
 * Created by kurmasz on 8/8/16.
 */
public class SampleUnsigned16BitAdderTest {

  //
  // Quick tests designed to quickly catch major errors.  (Also serve as example tests)
  //

  @Test
  public void zero_zero_false() {
    setPinUnsigned("InputA", 0);
    setPinUnsigned("InputB", 0);
    setPin("CarryIn", false);
    run();
    Assert.assertEquals(0, readPinUnsigned("Output"));
    Assert.assertFalse(readPin("CarryOut"));
  }

  @Test
  public void zero_one_false() {
    setPinUnsigned("InputA", 0);
    setPinUnsigned("InputB", 1);
    setPin("CarryIn", false);
    run();
    Assert.assertEquals(1, readPinUnsigned("Output"));
    Assert.assertFalse(readPin("CarryOut"));
  }

  @Test
  public void zero_zero_true() {
    setPinUnsigned("InputA", 0);
    setPinUnsigned("InputB", 0);
    setPin("CarryIn", true);
    run();
    Assert.assertEquals(1, readPinUnsigned("Output"));
    Assert.assertFalse(readPin("CarryOut"));
  }

  @Test
  public void zero_one_true() {
    setPinUnsigned("InputA", 0);
    setPinUnsigned("InputB", 1);
    setPin("CarryIn", true);
    run();
    Assert.assertEquals(2, readPinUnsigned("Output"));
    Assert.assertFalse(readPin("CarryOut"));
  }

  //
  // Thorough tests
  //
  // Note:  The technique below (cramming a bunch of different tests into one test method), is actually rather gross.
  // But, to the best of my knowledge, Java doesn't have a time-efficient, simple technique for putting each desired
  // test in its own method.
  //
  // Below, testIntegers is an array of integers that will serve as inputs to your adder.
  // The `verify` method takes two integers, and the carryIn as input, calculates the expect output, then runs the test.
  // the single test method `testAll` calls `verify` for each combination of integer inputs and carryIn


  // The complete list of integers to be tests.
  // (IMPORTANT:  !!! You need to add to this list !!!)
  private static final long testIntegers[] = {0, 1, 2, 13, 127, 128, 129, 0xAAAA, 65534, 65535};

  protected void verify(long a, long b, boolean carryIn) {

    long carryInAsInt = (carryIn ? 1 : 0);
    long expected = a + b + carryInAsInt;
    boolean expectedOverflow = expected > 65535;

    setPinUnsigned("InputA", a);
    setPinUnsigned("InputB", b);
    setPin("CarryIn", carryIn);
    run();
    String message = "of " + a + " + " + b + " with " + (carryIn ? "a " : " no ") + " carry in";

    // Output "wraps around" if there is an overflow
    Assert.assertEquals("Output " + message, (expected % 65536), readPinUnsigned("Output"));
    Assert.assertEquals("CarryOut " + message, expectedOverflow, readPin("CarryOut"));
  }

  @Test
  public void testAll() {
    int count = 0;
    for (long a : testIntegers) {
      for (long b : testIntegers) {
        verify(a, b, false);
        verify(a, b, true);
        count += 2;
      }
    }
    System.out.println("testAll ran " + count + " tests.");
  } // end testAll
}