package by.zharski.client.file;

import by.zharski.client.encription.EncryptionService;
import by.zharski.server.files.FileWrapper;
import org.apache.coyote.Response;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FileClient {

    private final String basePath;

    private final String encodedAuth;

    private final EncryptionService encryptionService;
    private final StorageService storageService;

    public FileClient(EncryptionService encryptionService, String basePath, String username, String password, StorageService storageService) {
        this.encryptionService = encryptionService;
        this.basePath = basePath;

        String auth = username + ":" + password;
        this.encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        this.storageService = storageService;
    }

    public List<String> getFileList() {
        ParameterizedTypeReference<List<byte[]>> parameterizedTypeReference = new ParameterizedTypeReference<>() {};

        List<byte[]> bytes = RestClient.create()
                .get()
                .uri(basePath + "/files")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                .retrieve()
                .body(parameterizedTypeReference);
        List<String> result = new ArrayList<>();
        for (byte[] byteFilename : bytes) {
            result.add(new String(encryptionService.decryptBytes(byteFilename)));
        }
        return result;
    }

    public void getFile(String filename) {
        ParameterizedTypeReference<byte[]> parameterizedTypeReference = new ParameterizedTypeReference<>() {};

        byte[] bytes = RestClient.create()
                .get()
                .uri(basePath + "/files/" + filename)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                .retrieve()
                .body(parameterizedTypeReference);
        storageService.store((FileWrapper) deserialize(encryptionService.decryptBytes(bytes)));
    }

    public void uploadFile(String filename) {
        ParameterizedTypeReference<byte[]> parameterizedTypeReference = new ParameterizedTypeReference<>() {};
        byte[] body = encryptionService.encryptBytes(serialize(storageService.loadAsFile(filename)));
        ResponseEntity<Void> response = RestClient.create()
                .post()
                .uri(basePath + "/files")
                .body(body)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                .retrieve()
                .toBodilessEntity();
    }

    static byte[] serialize(final Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
