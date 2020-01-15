package main_package.tools;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Maintenance {
    //C:\Users\Comarch\Downloads\Evolution-Generator-master\Evolution-Generator-master\src
    public final static String MAIN_PATH = "src/main/java";
    private final static String GIT_DIR = System.getProperty("user.dir") + "\\.git";
    private final static String MAJOR_VERSION = "1.";
    private final static String MINOR_VERSION = "6."; //w zaleznosci która fazę robimy
    public final static String VERSION_IDENTIFIER = getVersionIdentifier(); // pobranie wersji projektu

    public static String getVersionIdentifier() {
        String version;
        Repository repository = findGitRepository();
        int commitsCounter = findCommitsCounter(repository);
        String HEADIdentifier = findHEADIdentifier(repository);
        version = MAJOR_VERSION + MINOR_VERSION + commitsCounter + "_" + HEADIdentifier;
        return version;
    }

    private static Repository findGitRepository() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = null;
        try {
            repository = builder.setGitDir(new File(Maintenance.GIT_DIR))
                    .readEnvironment()
                    .findGitDir()
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repository;
    }

    private static int findCommitsCounter(Repository repository){
        Git git = new Git(repository);
        Iterable<RevCommit> log = null;
        AtomicInteger commitsCounter = new AtomicInteger();
        try {
            log = git.log().call();
            log.forEach(f->{
                commitsCounter.getAndIncrement();
            });
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return commitsCounter.get();
    }

    private static String findHEADIdentifier(Repository repository) {
        String HEADIdentifier = null;
        try {
            ObjectId head = repository.resolve("HEAD");
            HEADIdentifier = head.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HEADIdentifier;
    }
}
