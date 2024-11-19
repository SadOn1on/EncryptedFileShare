package by.zharski.client;

import by.zharski.client.file.FileClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

    private final FileClient fileClient;

    private final String INSTRUCTIONS =
            """
                    -h to get commands
                    -x to exit
                    -list to list all available files
                    -download {filename} to download file from server
                    -upload {filename} to upload file to the server""";

    public ClientApplication(FileClient fileClient) {
        this.fileClient = fileClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args).close();
    }

    @Override
    public void run(String... args) {
        System.out.println(INSTRUCTIONS);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] command = scanner.nextLine().split(" ");
            if (command.length > 2) {
                System.out.println("Wrong command");
                continue;
            }
            if (command[0].equals("-h")) {
                System.out.println(INSTRUCTIONS);
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
