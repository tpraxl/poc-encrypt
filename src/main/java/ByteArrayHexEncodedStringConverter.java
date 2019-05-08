import java.math.BigInteger;
import java.nio.charset.Charset;

public class ByteArrayHexEncodedStringConverter {

    public String ivToHexRepresentation(byte[] iv) {
        return this.encodeUsingBigIntegerStringFormat(iv);
    }
    public byte[] hexEncoded8ByteKeyToByteArray(String hexString) {
        return this.hexEncodedKeyToByteArray(hexString, 8);
    }
    public byte[] hexEncodedKeyToByteArray(String hexString, int numBytes) {
        byte[] result = new BigInteger(hexString, 16).toByteArray();
        result = this.handleSigned(result);

        byte[] padded = new byte[numBytes];
        System.arraycopy(result, 0, padded, padded.length-result.length, result.length);
        return padded;
    }

    private byte[] handleSigned(byte[] result) {
        if (result[0] == 0) {
            byte[] output = new byte[result.length - 1];
            System.arraycopy(
                    result, 1, output,
                    0, output.length);
            return output;
        }
        return result;
    }


    public String encodeUsingBigIntegerStringFormat(byte[] bytes) {
        // taken from https://www.baeldung.com/java-byte-arrays-hex-strings
        BigInteger bigInteger = new BigInteger(1, bytes);
        return String.format(
                "%0" + (bytes.length << 1) + "x", bigInteger);
    }
}