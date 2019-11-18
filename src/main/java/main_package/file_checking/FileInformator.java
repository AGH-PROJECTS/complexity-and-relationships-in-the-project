package main_package.file_checking;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class FileInformator {
    private String path;
    Map<String,Long> infoAboutFile = new HashMap<>();

    public FileInformator(String path) {
        this.path = path;
    }

    public Map<String, Long> getInformation() {


        File projectDirectory = new File(path);

        List<File> filesInProjectDir = Arrays.asList(projectDirectory.listFiles());

        List<File> sourceFiles = filesInProjectDir.stream()
                .filter(file -> file.toString().endsWith(".java"))
                .collect(Collectors.toList());

        filesInProjectDir.stream()
                .filter(
                        file -> file.isDirectory())
                .forEach(dir -> {
                    List<File> sourceFilesInDir = Arrays.asList(dir.listFiles()).stream()
                            .filter(file -> file.toString().endsWith(".java"))
                            .collect(Collectors.toList());
                    sourceFilesInDir.forEach(file -> sourceFiles.add(file));
                });

        sourceFiles.forEach(e-> {
            try {
                long lines = 0;
                Scanner scanner = new Scanner(e);
                while (scanner.hasNextLine()) {
                    scanner.nextLine();
                    lines++;
                }
                infoAboutFile.put(e.getName(),lines);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        //showInformation();
        return infoAboutFile;
    }

    /*private void showInformation() {
        infoAboutFile.entrySet()
                .forEach(file -> System.out.println("Name: " + file.getKey() +", size: " + file.getValue()));
    }*/
}
