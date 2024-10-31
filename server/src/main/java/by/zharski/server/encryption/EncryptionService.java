package by.zharski.server.encryption;

import by.zharski.server.RSA.RSA;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class EncryptionService {

    RSA rsa;
    BytesEncryptor encryptor;

    public EncryptionService(RSA rsa, BytesEncryptor encryptor) {
        this.rsa = rsa;
        this.encryptor = encryptor;
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

    public byte[] encryptFile(byte[] resource) {
        return encryptor.encrypt(resource);
    }

    public byte[] decryptFile(byte[] ciphertext) {
        return encryptor.decrypt(ciphertext);
    }

}
