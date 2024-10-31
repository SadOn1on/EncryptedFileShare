package by.zharski.client;

import by.zharski.client.RSA.RSA;
import by.zharski.client.encription.EncryptionService;
import by.zharski.client.encription.KeysClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

    private final KeysClient keysClient;

    private final RSA rsa;

    public ClientApplication(KeysClient keysClient, RSA rsa) {
        this.keysClient = keysClient;
        this.rsa = rsa;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String username = args[0];
        String password = args[1];


        List<List<BigInteger>> keys = keysClient.getEncryptionKes(username, password);
        EncryptionService encryptionService = new EncryptionService(rsa, keys);

    }
}
