package by.zharski.lab1.files;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
* Wrapper to conveniently serialize file's data and metadata
**/
public class FileWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private final byte[] bytes;
    private final File fileInfo;

    public FileWrapper(Path file) throws IOException {
        this.bytes = Files.readAllBytes(file);
        this.fileInfo = new File(file.toUri());
    }

    public String getFilename() {
        return fileInfo.getName();
    }

}
