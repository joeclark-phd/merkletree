package net.joeclark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

/**
 * Utility methods for hashing objects using SHA3.
 */
public class HashHelper {

    /** Hash a byte array using SHA3 */
    public static byte[] hash256( byte[] bytes ) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256( );
        return digestSHA3.digest( bytes );
    }
    
    /** Convert a byte array (i.e. a hash) into a String using hexadecimal notation. */
    public static String bytesToHex(byte[] digest) {
        return Hex.toHexString(digest);
    }

    /** 
     * Convert a serializable object to a byte array. 
     * Used in `hash256` but maybe also useful in finding out the size an object serializes to.
     * @throws Exception any kind of IOException, especially a NonSerializableException
     */
        public static byte[] getBytes(Serializable object) throws Exception {
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        try(ObjectOutputStream ooStream = new ObjectOutputStream(baoStream)) {
            ooStream.writeObject(object);
            ooStream.flush();
        } catch(NotSerializableException e) {
            throw new Exception("Attempted to serialize a non-serializable object.",e);
        } catch(IOException e) {
            e.printStackTrace();
            throw new Exception("Serializing an object failed for some unanticipated reason.",e);
        }
        return baoStream.toByteArray();
    }

    /**
     * Hash a serializable Object using SHA3.
     * @throws Exception any kind of IOException, most likely a NonSerializableException from trying to hash a non-Serializable object
     */
    public static byte[] hash256(Serializable object) throws Exception {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return digestSHA3.digest(getBytes(object));
    }

}
