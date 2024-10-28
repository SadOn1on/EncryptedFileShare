package by.zharski.lab1.files;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(FileWrapper file);

    Stream<Path> loadAll();

    Path load(String filename);

    FileWrapper loadAsFile(String filename);

    void deleteAll();

}