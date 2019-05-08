import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ByteArrayHexEncodedStringConverterTests {
    @Test
    public void it_converts_to_byte_array() {
        String hex = "0002030a0b0c0dff";
        byte[] expected = new byte[] { 0x0, 0x2, 0x3, 0x0a, 0x0b, 0x0c, 0x0d, (byte)0xff };
        ByteArrayHexEncodedStringConverter converter = new ByteArrayHexEncodedStringConverter();
        assertThat(converter.hexEncoded8ByteKeyToByteArray(hex), equalTo(expected));
    }
    @Test
    public void it_converts_hex_encoded_strings_back_and_forth() {
        String hex = "0123456789abcdef";
        ByteArrayHexEncodedStringConverter converter = new ByteArrayHexEncodedStringConverter();
        String actual = converter.ivToHexRepresentation(converter.hexEncoded8ByteKeyToByteArray(hex));
        assertThat(actual, equalTo(hex));
    }
}
