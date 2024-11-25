package by.client.file;

import by.client.encription.EncryptionService;
import by.server.files.FileWrapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class FileClient {

    private final String basePath;
    private final String encodedAuth;

    private final EncryptionService encryptionService;
    private final StorageService storageService;
    private final MessageDigest messageDigest;

    public FileClient(
            EncryptionService encryptionService,
            String basePath, String username,
            String password,
            StorageService storageService,
            MessageDigest messageDigest
    ) {
        this.encryptionService = encryptionService;
        this.basePath = basePath;
        this.messageDigest = messageDigest;

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
        try {
            ParameterizedTypeReference<byte[]> parameterizedTypeReference = new ParameterizedTypeReference<>() {};
            byte[] bytes = RestClient.create()
                    .get()
                    .uri(basePath + "/files/" + filename)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .retrieve()
                    .body(parameterizedTypeReference);
            List<byte[]> fileWithHash = (List<byte[]>) deserialize(encryptionService.decryptBytes(bytes));
            if (!Arrays.equals(fileWithHash.get(1), messageDigest.digest(fileWithHash.get(0)))) {
                System.out.println("Hash of the file does not match. File was not saved");
            }
            storageService.store((FileWrapper) deserialize(fileWithHash.get(0)));
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
        }
    }

    public void uploadFile(String filename) {
        try {
            FileWrapper fileWrapper = storageService.loadAsFile(filename);
            List<byte[]> body = List.of(
                    serialize(fileWrapper),
                    messageDigest.digest(serialize(fileWrapper))
            );
            ResponseEntity<Void> response = RestClient.create()
                    .post()
                    .uri(basePath + "/files")
                    .body(encryptionService.encryptBytes(serialize(body)))
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
        }
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
