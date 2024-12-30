package net.joeclark;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

public class HashHelperTest {

    /** Test that we can hash two identical byte arrays and they match */
    @Test
    public void testHashingByteArrays() {
        byte[] first = new byte[] { (byte) 0xe0, 0x4f, (byte) 0xd0, 0x20 };
        byte[] second = HexFormat.ofDelimiter(":").parseHex("e0:4f:d0:20");
        assertArrayEquals(first, second);
        // System.out.println(">>> first: "+HashHelper.bytesToHex(first));
        // System.out.println(">>> hash first: "+HashHelper.bytesToHex(HashHelper.hash256(first)));
        // System.out.println(">>> second: "+HashHelper.bytesToHex(second));
        // System.out.println(">>> hash second: "+HashHelper.bytesToHex(HashHelper.hash256(second)));
        assertArrayEquals(HashHelper.hash256(first), HashHelper.hash256(second));
        byte[] third = HexFormat.of().parseHex("e04fd021");
        // System.out.println(">>> third: "+HashHelper.bytesToHex(third));
        // System.out.println(">>> hash third: "+HashHelper.bytesToHex(HashHelper.hash256(third)));
        assertFalse(Arrays.equals(HashHelper.hash256(first), HashHelper.hash256(third)));
    }

    /** Test that we can convert bytes to hexadecimal strings consistently */
    @Test
    public void testConvertingHashToHex() {
        byte[] first = new byte[] { (byte) 0xe0, 0x4f, (byte) 0xd0, 0x20 };
        byte[] second = HexFormat.ofDelimiter(":").parseHex("e0:4f:d0:20");
        assertArrayEquals(first, second);
        assertEquals(HashHelper.bytesToHex(first), "e04fd020");
    }

    /** Test that we can hash serializable objects consistently */
    @Test
    public void testHashingObjects() throws Exception {

        String firstString = "four score and seven years ago...";
        String secondString = String.format("%s score and %s years ago...","four","seven");
        assertEquals(firstString, secondString);
        assertArrayEquals(HashHelper.hash256(firstString), HashHelper.hash256(secondString));

        // Trade is a serializable class in the test classpath
        Trade trade = new Trade("foo", "bar", 111);
        Trade trade2 = new Trade("foo", "bar", 111);
        assertNotEquals(trade, trade2); // objects are not equal because they don't implement .equals()
        byte[] tradeHash = HashHelper.hash256(trade);
        byte[] trade2Hash = HashHelper.hash256(trade2);
        assertArrayEquals(tradeHash, trade2Hash);
        assertEquals(HashHelper.bytesToHex(tradeHash), HashHelper.bytesToHex(trade2Hash));

    }

    /** 
     * Test the 'getBytes()' method on HashHelper.  
     * This is mainly for the dev to figure out what size an object will be once serialized.
     */
    @Test
    public void testSerializingObjects() throws Exception {
        // sender and receiver names are hashed to ensure consistent string size
        String sender = HashHelper.bytesToHex(HashHelper.hash256("foo"));
        String receiver = HashHelper.bytesToHex(HashHelper.hash256("bar"));
        Trade trade = new Trade(sender, receiver , 111);
        byte[] tradeBytes = HashHelper.getBytes(trade);
        // System.out.println(">>> size of Trade in bytes: "+tradeBytes.length);

        // The first time I tried this, the Trade serialized to 232 bytes.
        // If this test fails, it doesn't necessarily mean there's a "problem",
        // it just indicates that Java serialization may not always produce the
        // same size output.  On the other hand if it never fails, that suggests
        // serialization IS consistent in size.
        assertEquals(tradeBytes.length, 232);

        // Now see if a second Trade is the same size.
        String sender2 = HashHelper.bytesToHex(HashHelper.hash256("darmok"));
        String receiver2 = HashHelper.bytesToHex(HashHelper.hash256("jalaad"));
        Trade trade2 = new Trade(sender2, receiver2 , 1401);
        byte[] trade2Bytes = HashHelper.getBytes(trade);
        // System.out.println(">>> size of trade2 in bytes: "+trade2Bytes.length);
        assertEquals(trade2Bytes.length, tradeBytes.length);
    }


}
