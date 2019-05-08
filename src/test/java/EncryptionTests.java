import org.junit.Test;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class EncryptionTests {
    static final String payload = "Payload";
    static final Charset ascii = Charset.forName("ASCII");

    @Test
    public void it_can_encrypt_and_decrypt_using_a_random_16byte_key() throws GeneralSecurityException {
        byte[] key = createRandom16ByteKey();
        assertEncryptedDecryptedEqualsOriginalUsing(key);
    }

    private byte[] createRandom16ByteKey() {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        return key;
    }

    private void assertEncryptedDecryptedEqualsOriginalUsing(byte[] key) throws GeneralSecurityException {
        assertThat(
            // decrypt
            decrypt(
                key,
                // encrypted payload
                encrypt(key)
            ),
            // equals
            equalTo(
                // base payload
                payload.getBytes(ascii)
            )
        );
    }

    private Encoded encrypt(byte[] key) throws GeneralSecurityException {
        return new BlowfishEncoderDecoderPOC().encrypt(
            key,
            payload.getBytes(ascii)
        );
    }

    private byte[] decrypt(byte[] key, Encoded result) throws GeneralSecurityException {
        return new BlowfishEncoderDecoderPOC().decrypt(
            key,
            result
        );
    }

    @Test
    public void it_can_handle_a_string_based_secret_key() throws GeneralSecurityException {
        // notice, this is the hex code representation of a 16 byte key, represented as a string of 32 characters.
        // It cannot simply be converted to a byte array.
        String hexEncoded16ByteKey = "cafebabecafebabecafebabecafebabe";
        byte[] key = new ByteArrayHexEncodedStringConverter()
                .hexEncodedKeyToByteArray(hexEncoded16ByteKey, 16);
        assertEncryptedDecryptedEqualsOriginalUsing(key);
    }

    @Test
    public void it_can_concatenate_the_encryption_result() throws GeneralSecurityException {
        String hexEncoded16ByteKey = "cafebabecafebabecafebabecafebabe";

        byte[] key = new ByteArrayHexEncodedStringConverter()
                .hexEncodedKeyToByteArray(hexEncoded16ByteKey, 16);

        Encoded result = encrypt(key);

        String cat = result.concatenateAndEncode();

        byte[] decryptedPayload = new BlowfishEncoderDecoderPOC().decryptConcatenated(key, cat);
        assertThat(decryptedPayload, equalTo(this.payload.getBytes(ascii)));
    }

    @Test
    public void it_can_concatenate_and_parse() {
        byte[] iv = new byte[BlowfishEncoderDecoderPOC.IV_BYTE_SIZE];
        new SecureRandom().nextBytes(iv);

        byte[] encryptedPayload = "EncryptedPayload".getBytes(Charset.forName("ASCII"));

        Encoded encoded = Encoded.builder().withIv(iv).withEncryptedPayload(encryptedPayload).build();

        String concatenated = encoded.concatenateAndEncode();
        Encoded parsed = Encoded.parseConcatenated(concatenated);

        assertThat(parsed.getIv(), equalTo(encoded.getIv()));
        assertThat(parsed.getEncryptedPayload(), equalTo(encoded.getEncryptedPayload()));
    }
}

