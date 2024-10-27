package by.zharski.lab1.encryption;

import by.zharski.lab1.RSA.RSA;
import org.springframework.core.io.ByteArrayResource;
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

    public List<BigInteger> encryptKey(String key) {
        byte[] keyBytes = key.getBytes();
        List<BigInteger> result = new ArrayList<>();
        for (byte x : keyBytes) {
            result.add(rsa.encr(new BigInteger(String.valueOf(x))));
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
