package by.zharski.client;

import by.zharski.client.RSA.RSA;
import by.zharski.client.encription.EncryptionService;
import by.zharski.client.encription.KeysClient;
import by.zharski.client.file.FileClient;
import by.zharski.client.file.StorageProperties;
import by.zharski.client.file.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ClientApplication implements CommandLineRunner {

    private final StorageService storageService;

    private final KeysClient keysClient;

    private final RSA rsa;

    private final String instruction =
            """
                    -h to get commands
                    -x to exit
                    -list to list all available files
                    -download {filename} to download file from server
                    -upload {filename} to upload file to the server""";

    public ClientApplication(KeysClient keysClient, RSA rsa, StorageService storageService) {
        this.keysClient = keysClient;
        this.rsa = rsa;
        this.storageService = storageService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        String username = args[0];
        String password = args[1];

        List<List<BigInteger>> keys = keysClient.getEncryptionKes(username, password);
        EncryptionService encryptionService = new EncryptionService(rsa, keys);
        FileClient fileClient = new FileClient(encryptionService, keysClient.getBasePath(), username, password, storageService);

        System.out.println("Keys received successfully");
        System.out.println(instruction);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] command = scanner.nextLine().split(" ");
            if (command.length > 2) {
                System.out.println("Wrong command");
                continue;
            }
            if (command[0].equals("-h")) {
                System.out.println(instruction);
            } else if (command[0].equals("-list")) {
                fileClient.getFileList().forEach(System.out::println);
            } else if (command[0].equals("-x")) {
                return;
            } else if (command[0].equals("-download")) {
                if (command.length < 2) {
                    System.out.println("Wrong command");
                    continue;
                }
                fileClient.getFile(command[1]);
            } else if (command[0].equals("-upload")) {
                if (command.length < 2) {
                    System.out.println("Wrong command");
                    continue;
                }
                fileClient.uploadFile(command[1]);
            }
        }
    }
}
