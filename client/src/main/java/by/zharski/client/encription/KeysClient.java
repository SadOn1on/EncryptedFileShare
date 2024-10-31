package by.zharski.client.encription;

import by.zharski.client.RSA.RSA;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class KeysClient {

    private String basePath;

    private RSA rsa;

    public KeysClient(String basePath, RSA rsa) {
        this.basePath = basePath;
        this.rsa = rsa;
    }

    public List<List<BigInteger>> getEncryptionKes(String username, String password) {
        ParameterizedTypeReference<List<List<BigInteger>>> parameterizedTypeReference =
                new ParameterizedTypeReference<>() {};
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        return RestClient.create()
                .get()
                .uri(basePath + "/keys?k=" + rsa.getE().toString() + "&n=" + rsa.getN().toString())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                .retrieve()
                .body(parameterizedTypeReference);
    }
}
