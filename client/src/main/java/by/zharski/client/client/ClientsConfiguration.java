package by.zharski.client.client;

import by.zharski.client.ClientApplication;
import by.zharski.client.RSA.RSA;
import by.zharski.client.encription.KeysClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({ "classpath:application.properties", "classpath:application-${spring.profiles.active}.properties"})
public class ClientsConfiguration {

    @Value("${client.base.path}")
    private String basePath;

    private RSA rsa;

    public ClientsConfiguration(RSA rsa) {
        this.rsa = rsa;
    }

    @Bean
    public KeysClient keysClient() {
        return new KeysClient(basePath, rsa);
    }
}
