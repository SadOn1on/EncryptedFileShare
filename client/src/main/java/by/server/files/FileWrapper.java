package by.server.files;

import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
* Wrapper to conveniently serialize file's data and metadata
**/
@EqualsAndHashCode
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

    public boolean isEmpty() { return bytes.length == 0; }

    public File getFileInfo() { return this.fileInfo; }

    public byte[] getBytes() { return this.bytes; }

}
