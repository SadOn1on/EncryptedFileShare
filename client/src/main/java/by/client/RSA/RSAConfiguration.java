package by.client.RSA;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({ "classpath:application.properties", "classpath:application-${spring.profiles.active}.properties"})
public class RSAConfiguration {

    @Value("${rsa.encryption.strength}")
    private int RSAStrength;

    @Bean
    public RSA rsa() {
        return new RSA(RSAStrength);
    }

}
