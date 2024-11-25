package by.server.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/keys")
@PropertySource({ "classpath:application.properties", "classpath:application-${spring.profiles.active}.properties"})
public class KeysController {

    @Value("${aes.password}")
    private String password;
    @Value("${aes.salt}")
    private String salt;

    private EncryptionService encryptionService;

    public KeysController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @GetMapping
    public List<List<BigInteger>> getKey(@RequestParam String k, @RequestParam String n) {
        return List.of(encryptionService.encryptKey(password, k, n), encryptionService.encryptKey(salt, k, n));
    }

}
