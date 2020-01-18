package main_package.tools;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RevisionDifference {
    private static final String RESOURCES = System.getProperty("user.dir") + "\\src\\main\\resources\\clone";
    public static final String PATH = RESOURCES + "\\src\\main\\java";
    private static final String REMOTE_REPOSITORY = "https://github.com/dawidkruczek/projectIO.git";

    public static Map<String,String> findDifferences3(Map<String, String> old, Map<String,String> current){
        Map<String, String> diff = new HashMap<>();
        Set<Map.Entry<String, String>> entrySet = current.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (!old.containsKey(entry.getKey())) {
                diff.put("*NEW*" + entry.getKey(), entry.getValue());
            } else {
                diff.put(entry.getKey(), entry.getValue());
            }
        }
        return diff;
    }

    public static Map<String, Map<String, AtomicInteger>> findDifferences2(Map<String, Map<String, AtomicInteger>> old, Map<String, Map<String, AtomicInteger>> current) {
        Map<String, Map<String, AtomicInteger>> diff = new HashMap<>();
        Set<Map.Entry<String, Map<String, AtomicInteger>>> entrySet = current.entrySet();
        for (Map.Entry<String, Map<String, AtomicInteger>> entry : entrySet) {
            if (!old.containsKey(entry.getKey())) {
                diff.put("*NEW*" + entry.getKey(), entry.getValue());
            } else {
                diff.put(entry.getKey(), entry.getValue());
            }
        }
        return diff;
    }

    public static Map<String, Integer> findDifferences(Map<String, Integer> old, Map<String, Integer> current) {
        Map<String, Integer> diff = new HashMap<>();
        Set<Map.Entry<String, Integer>> entrySet = current.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (!old.containsKey(entry.getKey())) {
                diff.put("*NEW*" + entry.getKey(), entry.getValue());
            } else {
                diff.put(entry.getKey(), entry.getValue());
            }
        }
        return diff;
    }

    public static void getRepository() throws GitAPIException, IOException {
        File file = new File(RESOURCES);
        if (!file.exists())
            file.mkdir();
        deleteDirectory(file);
        Git git = Git.cloneRepository()
                .setURI(REMOTE_REPOSITORY)
                .setDirectory(file)
                .call();

        List<RevCommit> merges = new ArrayList<>();
        Iterable<RevCommit> logs = git.log().all().call();
        for (RevCommit rev : logs) {
            if (rev.getShortMessage().contains("Merge")) {
                merges.add(rev);
            }
        }
        if (merges.size() > 2)
            git.checkout().setName(merges.get(1).getId().getName()).call();
    }

    private static void deleteDirectory(File file) throws IOException {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirectory(entry);
                }
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }
}
