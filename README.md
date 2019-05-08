# poc-encrypt

Just a POC that shows blowfish cbc encryption as specified below is possible with java.

## Setup / Test

Just run `./gradlew clean test -i` in the project root.

## Encryption specs

A compatible value for the parameter is created as follows:

1. Use the blowfish cipher `bf-cbc`
2. with a **random** IV (initialization vector) of 8 bytes, encoded into 16 hex-digits
3. and with the secret key `cafebabecafebabecafebabecafebabe` (substitute with the real key)
4. base64 encode the encrypted result
5. concatenate the IV and the base64 encoded encrypted result

**Pseudo code:**

```
IV = create_8_random_bytes_and_encode_as_16_char_hex_string();
KEY = "cafebabecafebabecafebabecafebabe"
CIPHER = 'bf-cbc';
DATA = "My data to be encoded";

# USE NO SALT!
ENCRYPTED = encode(CIPHER, KEY, IV, DATA);
ENCODED = base64_encode(ENCRYPTED);

print(IV + ENCODED);
```

**Bash script for encoding "encrypt.sh"**

```bash
#!/usr/bin/env bash

####
#
# Usage: export KEY=cafebabecafebabecafebabecafebabe; ./encrypt.sh $(cat user.json)
#
###

# Generate 8 random bytes and encode them hexadecimal
# e.g. 0123456789abcdef
IV=$(openssl rand -hex 8)

# HEX REPRESENTATION OF KEY, shared with foreign application
# e.g. cafebabecafebabecafebabecafebabe
KEY="${KEY}"

# Use Blowfish CBC
CIPHER="bf-cbc"

# DATA, RAW, passed as first argument
DATA="${1}"

# encrypt data and return base64 encoded result
ENCRYPTED=$(echo -n "${DATA}" | \
openssl enc -${CIPHER} -nosalt -K "${KEY}" -iv "${IV}" | base64 --wrap=0)

# output initialization vector hex representation concatenated
# with base64 encoded bf-cbc encryption
echo -n "${IV}${ENCRYPTED}"
```

## Testing compatible encryption

Use the following script for decryption and verify that your encrypted value gets decrypted as expected.

**"decrypt.sh"**

```bash
#!/usr/bin/env bash

####
#
# Usage:
# export KEY=cafebabecafebabecafebabecafebabe; $(cat encrypted.json) | ./decrypt.sh
#
###

# Read first 16 characters as hex representation
# of 8 byte initialization vector
# e.g. 0123456789abcdef
IV=$(head -c16)

# HEX REPRESENTATION OF KEY, shared with foreign application
# e.g. cafebabecafebabecafebabecafebabe
KEY="${KEY}"

# Use Blowfish CBC
CIPHER="bf-cbc"

# decrypt payload and return string
DECRYPTED=$(base64 --decode --wrap=0 - | openssl enc -${CIPHER} -d -nosalt -K "${KEY}" -iv "${IV}")

# output decrypted result
echo "${DECRYPTED}"
```
