package mdpalgo.constants;

/**
 * Constants for communication
 */
public class CommConstants {
    public static final String EX_START = "alg:explore";   // Android --> PC
    public static final String FP_START = "alg:fast";   // Android --> PC
    public static final String MAP = "MAP";             // PC --> Android
    public static final String BOT_POS = "BOT_POS";     // PC --> Android
    public static final String BOT_START = "BOT_START"; // PC --> Arduino
    public static final String INSTR = "INSTR";         // PC --> Arduino
    public static final String SDATA = "obs";         // Arduino --> PC
    public static final String IMAGE = "img";           // PC --> RPi
    public static final String TNONE = "none";      // RPi --> PC
    public static final String TFOUND = "found";    // RPi --> PC
    public static final String TNOT = "not";         // RPi --> PC
    public static final String TEST_CONNECTION = "test";
}
