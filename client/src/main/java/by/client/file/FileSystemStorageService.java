package by.client.file;

import by.server.files.FileWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
@PropertySource({ "classpath:application.properties", "classpath:application-${spring.profiles.active}.properties"})
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    public FileSystemStorageService(@Value("${storage.folder.path}") String path) {
        if(path.trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }
        this.rootLocation = Paths.get(path);
    }

    @Override
    public void store(FileWrapper file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                        Paths.get(file.getFilename())
                    )
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException("Cannot store file outside current directory.");
            }
            Files.write(destinationFile, file.getBytes());

        }
        catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public FileWrapper loadAsFile(String filename) {
        try {
            Path file = load(filename);
            if (Files.exists(file) || Files.isReadable(file)) {
                return new FileWrapper(file);
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
