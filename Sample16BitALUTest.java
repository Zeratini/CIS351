import org.junit.Assert;
import org.junit.Test;
import java.util.function.BiFunction;

import static edu.gvsu.dlunit.DLUnit.*;

/**
 * Test cases for a signed 16-bit ALU.
 * <p>
 * IMPORTANT:  These test cases do *not* thoroughly test the circuit.  You need to
 * re-name this class and add more tests!
 * <p>
 * <p>
 * Created by kurmasz on 8/8/16.
 */
public class Sample16BitALUTest {

  /*
   * Test SubU
   * Test Xor - Works b/c And works (identical)
   * Test Or - Works b/c And works (identical)
   * Test lui - Works b/c Not works (very similar)
   */

  public static class OpCodes {
    public static final int ADDU = 0;
    public static final int SUBU = 1;
    public static final int AND  = 2;
    public static final int OR   = 3;
    public static final int NOT  = 4;
    public static final int XOR  = 5;
    public static final int LUI  = 6;
    public static final int SLTU = 7;
    public static final int ADD  = 8;
    public static final int SUB  = 9;
    public static final int SLT  = 15;
  }


  /***Testing Addu/Subu***/
  @Test
  public void testAddu() {
    setPinUnsigned("InputA", 53400);
    setPinUnsigned("InputB", 53500);
    setPinUnsigned("Op", OpCodes.ADDU);
    run();
    Assert.assertEquals("Addition Output", (53400 + 53500) % 65536, readPinUnsigned("Output"));

    // Overflow for unsigned addition is false by definition
    Assert.assertEquals("Addition Overflow", false, readPin("Overflow"));
  }

  @Test
  public void testSubu() {
    setPinUnsigned("InputA", 53400);
    setPinUnsigned("InputB", 53500);
    setPinUnsigned("Op", OpCodes.SUBU);
    run();
    Assert.assertEquals("Subtration Output", 65536 + (53400 - 53500), readPinUnsigned("Output"));

    // Overflow for unsigned addition is false by definition
    Assert.assertEquals("Addition Overflow", false, readPin("Overflow"));
  }

  @Test
  public void testSubu2() {
    setPinUnsigned("InputA", 53500);
    setPinUnsigned("InputB", 53400);
    setPinUnsigned("Op", OpCodes.SUBU);
    run();
    Assert.assertEquals("Subtration Output", (53500 - 53400) % 65536, readPinUnsigned("Output"));

    // Overflow for unsigned addition is false by definition
    Assert.assertEquals("Addition Overflow", false, readPin("Overflow"));
  }

  /***Testing Add/Sub***/
  @Test
  public void testAddition() {
    setPinSigned("InputA", 23);
    setPinSigned("InputB", 44);
    setPinUnsigned("Op", OpCodes.ADD);
    run();
    Assert.assertEquals("Addition Output", 23 + 44, readPinSigned("Output"));
    Assert.assertEquals("Addition Overflow", false, readPin("Overflow"));
  }

  public void testAdd(long a, long b) {
    setPinSigned("InputA", a);
    setPinSigned("InputB", b);
    setPinUnsigned("Op", OpCodes.ADD);

    long expected = a + b;
    boolean expectedOverflow = ((expected >= (1 << 15)) || (expected < -(1 << 15)));

    // Output "wraps around" if there is an overflow
    if (expectedOverflow && expected > 0) {
      expected -= 65536;
    } else if (expectedOverflow && expected < 0) {
      expected += 65536;
    }

    run();
    Assert.assertEquals("Addition Output with " + a + " plus " + b, expected, readPinSigned("Output"));

    Assert.assertEquals("Addition Overflow with " + a + " plus " + b, expectedOverflow, readPin("Overflow"));
  }

  public void testSubtraction(long a, long b) {
    setPinSigned("InputA", a);
    setPinSigned("InputB", b);
    setPinUnsigned("Op", OpCodes.SUB);

    long expected = a - b;
    boolean expectedOverflow = ((expected >= (1 << 15)) || (expected < -(1 << 15)));

    // Output "wraps around" if there is an overflow
    if (expectedOverflow && expected > 0) {
      expected -= 65536;
    } else if (expectedOverflow && expected < 0) {
      expected += 65536;
    }

    run();
    Assert.assertEquals("Subtraction Output with " + a + " minus " + b, expected, readPinSigned("Output"));

    Assert.assertEquals("Subtraction Overflow with " + a + " minus " + b, expectedOverflow, readPin("Overflow"));
  }

  public static final long testIntegers[] = {-32768, -32767, 0, 1, 2, 13, 127, 128, 129, 0x5555, 32766, 32767, -1, -2, -3};

  @Test
  public void testAllAddSub() {
    for (long a : testIntegers) {
      for (long b : testIntegers) {
        testSubtraction(a, b);
        testAdd(a,b);
      }
    }
  }

  /***Testing SLT***/

  @Test
  public void ltSigned() {
    setPinSigned("InputA", 5);
    setPinSigned("InputB", 6);
    setPinUnsigned("Op", OpCodes.SLT);
    run();
    Assert.assertEquals("Signed Less Than Output", 1, readPinSigned("Output"));
    Assert.assertEquals("Signed Less Than Overflow", false, readPin("Overflow"));
  }

  @Test
  public void ltSigned2() {
    setPinSigned("InputA", 32767);
    setPinSigned("InputB", -1);
    setPinUnsigned("Op", OpCodes.SLT);
    run();
    Assert.assertEquals("Signed Less Than Output", 0, readPinSigned("Output"));
    Assert.assertEquals("Signed Less Than Overflow", false, readPin("Overflow"));
  }


  public static void verifySigned(long a, long b, boolean checkOverflow) {
    long expected = (a < b) ? 1 : 0;

    setPinSigned("InputA", a);
    setPinSigned("InputB", b);
    setPinUnsigned("Op", OpCodes.SLT);
    run();
    String message = String.format(" of %d < %d (signed) ", a, b);
    Assert.assertEquals("Output" + message, expected, readPinUnsigned("Output"));
    if (checkOverflow) {
      Assert.assertEquals("Overflow" + message, false, readPin("Overflow"));
    }
  }


  @Test
  public void ltSigned_allPairs() {
    long[] values = {-32768, -32767, -1, 0, 1, 32766, 32767};
    for (long a : values) {
      for (long b : values) {
        verifySigned(a, b, true);
      }
    }
  }

  public static void verifyUnsigned(long a, long b, boolean checkOverflow) {
    long expected = (a < b) ? 1 : 0;

    setPinUnsigned("InputA", a);
    setPinUnsigned("InputB", b);
    setPinUnsigned("Op", OpCodes.SLTU);
    run();
    String message = String.format(" of %d < %d (unsigned) ", a, b);
    Assert.assertEquals("Output" + message, expected, readPinUnsigned("Output"));
    if (checkOverflow) {
      Assert.assertEquals("Overflow" + message, false, readPin("Overflow"));
    }
  }

  @Test
  public void ltUnsigned_allPairs() {
    long[] values = {0, 1, 2, 65534, 65535};
    for (long a : values) {
      for (long b : values) {
        verifyUnsigned(a, b, true);
      }
    }
  }

  private void verifyLogic(String name, int op, long a, long b, BiFunction<Long, Long, Long> func) {
    setPinUnsigned("InputA", a);
    setPinUnsigned("InputB", b);
    setPinUnsigned("Op", op);
    run();
    String message = String.format("0x%x %s 0x%x", a, name, b);
    Assert.assertEquals(message, (long)func.apply(a, b), readPinUnsigned("Output"));
    Assert.assertFalse(message + " overflow", readPin("Overflow"));
  }

  @Test
  public void testAnd() {
    verifyLogic("and", OpCodes.AND, 0xFF00, 0x0F0F, (a, b) -> a & b);
  }

  @Test // The mask in the lambda sets bits above 16 to 0 so that Java effectively treats all results as unsigned
  public void testNot() {
    verifyLogic("not", OpCodes.NOT, 0x1, 0x0F0F, (a, b) -> (~a) & 0xFFFF);
  }
}
