import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class BlowfishEncoderDecoderPOC {
    public static final int IV_BYTE_SIZE = 8;

    Encoded encrypt(byte[] secretKey, byte[] payload) throws java.security.GeneralSecurityException {
        byte[] iv = new byte[IV_BYTE_SIZE];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encrypted = cipher.doFinal(payload);
        return Encoded.builder().withIv(iv).withEncryptedPayload(encrypted).build();
    }

    public byte[] decrypt(byte[] secretKey, Encoded result) throws java.security.GeneralSecurityException {
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, result.getIv());
        return cipher.doFinal(result.getEncryptedPayload());
    }

    private Cipher initCipher(int mode, byte[] secretKey, byte[] iv) throws GeneralSecurityException {
        SecretKey key = new SecretKeySpec(secretKey, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(mode, key, ivSpec);
        return cipher;
    }

    public byte[] decryptConcatenated(byte[] key, String cat) throws GeneralSecurityException {
        return decrypt(
            key,
            Encoded.parseConcatenated(cat)
        );
    }
}
