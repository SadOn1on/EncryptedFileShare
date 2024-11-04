package by.zharski.server.files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.List;

import by.zharski.server.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final StorageService storageService;
    private final EncryptionService encryptionService;

    @Autowired
    public FileUploadController(StorageService storageService, EncryptionService encryptionService) {
        this.storageService = storageService;
        this.encryptionService = encryptionService;
    }

    @GetMapping
    public List<byte[]> listUploadedFiles() {
        return storageService.loadAll()
                .map(Path::toString)
                .map(String::getBytes)
                .map(encryptionService::encryptBytes)
                .toList();
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) {
        FileWrapper file = storageService.loadAsFile(filename);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\""
                )
                .body(encryptionService.encryptBytes(serialize(file)));
    }

    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestBody byte[] file) {
        FileWrapper fileWrapper = (FileWrapper) deserialize(encryptionService.decryptBytes(file));
        storageService.store(fileWrapper);

        return ResponseEntity.ok().body(fileWrapper.getFilename());
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
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