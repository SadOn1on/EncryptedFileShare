package by.zharski.server.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
@PropertySource({ "classpath:application.properties", "classpath:application-${spring.profiles.active}.properties"})
public class EncryptionConfiguration {

    @Value("${aes.password:test}")
    private String password;
    @Value("${aes.salt}")
    private String salt;

    @Bean
    public BytesEncryptor bytesEncryptor() {
        return Encryptors.standard(password, salt);
    }

    @Bean
    public MessageDigest messageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }

}
