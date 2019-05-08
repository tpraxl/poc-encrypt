import java.util.Base64;

public class Encoded {
    private byte[] iv;
    private byte[] encryptedPayload;

    private Encoded() {

    }

    public static Builder builder() {
        return new Builder();
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getEncryptedPayload() {
        return encryptedPayload;
    }

    public String concatenateAndEncode() {
        String ivHexRepresentation = new ByteArrayHexEncodedStringConverter().ivToHexRepresentation(iv);
        return ivHexRepresentation + Base64.getEncoder().encodeToString(encryptedPayload);
    }

    public static Encoded parseConcatenated(String cat) {
        // 16 characters that hex encode an 8 byte iv
        String ivHex = cat.substring(0, 16);
        byte[] iv = new ByteArrayHexEncodedStringConverter().hexEncoded8ByteKeyToByteArray(ivHex);

        // base64 encoded encrypted payload
        String encryptedBase64Payload = cat.substring(16);
        // base64 decode
        byte[] encryptedPayload = Base64.getDecoder().decode(encryptedBase64Payload);
        return builder().withIv(iv).withEncryptedPayload(encryptedPayload).build();
    }

    public static class Builder {
        Encoded result;

        public Builder() {
            result = new Encoded();
        }

        public Builder withIv(byte[] iv) {
            result.iv = iv;
            return this;
        }
        public Builder withEncryptedPayload(byte[] encryptedPayload) {
            result.encryptedPayload = encryptedPayload;
            return this;
        }
        public Encoded build() {
            return result;
        }
    }
}
