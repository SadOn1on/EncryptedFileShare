package by.client.client;

import by.client.RSA.RSA;
import by.client.encription.EncryptionService;
import by.client.encription.KeysClient;
import by.client.file.FileClient;
import by.client.file.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Configuration
@PropertySource({ "classpath:application.properties", "classpath:application-${spring.profiles.active}.properties"})
public class ClientsConfiguration {

    @Value("${client.base.path}")
    private String basePath;

    private final RSA rsa;

    private final ApplicationArguments args;

    private final StorageService storageService;

    public ClientsConfiguration(RSA rsa, ApplicationArguments args, StorageService storageService) {
        this.rsa = rsa;
        this.args = args;
        this.storageService = storageService;
    }

    @Bean
    public KeysClient keysClient() {
        return new KeysClient(basePath, rsa);
    }

    @Bean
    public EncryptionService encryptionService(KeysClient keysClient) {
        String username = args.getSourceArgs()[0];
        String password = args.getSourceArgs()[1];
        List<List<BigInteger>> keys = keysClient.getEncryptionKes(username, password);
        return new EncryptionService(rsa, keys);
    }

    @Bean
    public FileClient fileClient(
            EncryptionService encryptionService,
            KeysClient keysClient,
            MessageDigest messageDigest
    ) {
        String username = args.getSourceArgs()[0];
        String password = args.getSourceArgs()[1];
        return new FileClient(
                encryptionService,
                keysClient.getBasePath(),
                username,
                password,
                storageService,
                messageDigest
        );
    }

    @Bean
    public MessageDigest messageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }
}
