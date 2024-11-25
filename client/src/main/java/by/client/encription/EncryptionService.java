package by.client.encription;

import by.client.RSA.RSA;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class EncryptionService {

    RSA rsa;
    BytesEncryptor encryptor;

    public EncryptionService(RSA rsa, List<List<BigInteger>> encryptedKeys) {
        this.rsa = rsa;
        this.encryptor = Encryptors.standard(decryptKey(encryptedKeys.get(0)), decryptKey(encryptedKeys.get(1)));
    }

    public List<BigInteger> encryptKey(String key, String rsaKey, String modulus) {
        byte[] keyBytes = key.getBytes();
        List<BigInteger> result = new ArrayList<>();
        BigInteger rsaKeyBigInt = new BigInteger(rsaKey);
        BigInteger n = new BigInteger(modulus);
        for (byte x : keyBytes) {
            result.add(RSA.encr(rsaKeyBigInt, new BigInteger(String.valueOf(x)), n));
        }
        return result;
    }

    public String decryptKey(List<BigInteger> cipher) {
        byte[] result = new byte[cipher.size()];
        for (int i = 0; i < cipher.size(); ++i) {
            result[i] = (byte) rsa.decr((cipher.get(i))).intValue();
        }
        return new String(result);
    }

    public byte[] encryptBytes(byte[] resource) {
        return encryptor.encrypt(resource);
    }

    public byte[] decryptBytes(byte[] ciphertext) {
        return encryptor.decrypt(ciphertext);
    }

}

