package main_package.tools;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RevisionDifference {
    private static String RESOURCES;
    public static String PATH;
    private String REMOTE_REPOSITORY;
    private static Iterable<RevCommit> logs;
    private static Git git;
    private static int i = 0;

    public RevisionDifference(String remoteRepo) {
        RESOURCES = System.getProperty("user.dir") + "\\src\\main\\resources\\downloads\\clone_" + i++;
        PATH = RESOURCES + "\\src\\main\\java";
        this.REMOTE_REPOSITORY = remoteRepo;
    }

    public void setPATH(String PATH) {
        RevisionDifference.PATH = RESOURCES + PATH;
    }

    public Map<String,String> findDifferences3(Map<String, String> old, Map<String,String> current){
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

    public Map<String, Map<String, AtomicInteger>> findDifferences2(Map<String, Map<String, AtomicInteger>> old, Map<String, Map<String, AtomicInteger>> current) {
        Set<String> oldOnes = new LinkedHashSet<>();

        for (Map.Entry<String, Map<String, AtomicInteger>> entry : old.entrySet()) {
            oldOnes.addAll(entry.getValue().keySet());
        }

        Map<String, Map<String, AtomicInteger>> diff2 = new HashMap<>();
        for (Map.Entry<String, Map<String, AtomicInteger>> entry : current.entrySet()) {
            Map<String, AtomicInteger> sub = new HashMap<>();
            for (Map.Entry<String, AtomicInteger> littleEntry : entry.getValue().entrySet()) {
                if (!oldOnes.contains(littleEntry.getKey())) {
                    sub.put("*NEW*" + littleEntry.getKey(), littleEntry.getValue());
                } else {
                    sub.put(littleEntry.getKey(), littleEntry.getValue());
                }
            }
            if (!old.containsKey(entry.getKey())) {
                diff2.put("*NEW*" + entry.getKey(), sub);
            } else {
                diff2.put(entry.getKey(), sub);
            }
        }

        return diff2;
    }

    public Map<String, Integer> findDifferences(Map<String, Integer> old, Map<String, Integer> current) {
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

    public void getRepository() throws GitAPIException, IOException {
        File file = new File(RESOURCES);
        if (!file.exists())
            file.mkdir();
        deleteDirectory(file);
        git = Git.cloneRepository()
                .setURI(REMOTE_REPOSITORY)
                .setDirectory(file)
                .call();
    }

    public void goToPreviousMerge() throws GitAPIException, IOException {
        List<RevCommit> merges = new ArrayList<>();
        List<RevCommit> commits = new ArrayList<>();
        logs = git.log().all().call();
        for (RevCommit rev : logs) {
            commits.add(rev);
            if (rev.getShortMessage().contains("Merge")) {
                merges.add(rev);
            }
        }
        if (merges.size() > 2)
            git.checkout().setName(merges.get(1).getId().getName()).call();
        else
            git.checkout().setName(commits.get(1).getId().getName()).call();

    }

    private void deleteDirectory(File file) throws IOException {
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
