package Nikk;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Search {
    private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    private static final ConcurrentHashMap<String, Path> results = new ConcurrentHashMap<>();

    public static Map<Integer, String> searchDisks(){

        Map<Integer, String> roots = new HashMap<Integer, String>();

        int i = 1;
        for(File file : File.listRoots()){
            roots.put(i, fileSystemView.getSystemDisplayName(file));
            i++;
        }

        return roots;
    }
    public ConcurrentHashMap<String, Path> searchFile(String fileToSearch) {
        results.clear(); 
        ExecutorService executorService = Executors.newCachedThreadPool();
        Path diskPath = Paths.get(Display.getDisk() + "\\"); 
        try {
            Files.walkFileTree(diskPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                    if (!Files.isReadable(dir)) {
                        System.err.println("Директория недоступна: " + dir);
                        return FileVisitResult.SKIP_SUBTREE;
                    }

                    System.out.println("Обрабатывается директория: " + dir);
                    executorService.submit(() -> processPath(dir, fileToSearch));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    executorService.submit(() -> processPath(file, fileToSearch));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {
                    if (e instanceof AccessDeniedException) {
                        System.err.println("Доступ запрещен: " + file);
                    }
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });

            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.HOURS);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }

        return results;
    }

    private static void processPath(Path path, String fileToSearch) {
        try {
            String fileName = (path.getFileName() == null) 
                ? path.toString()   
                : path.getFileName().toString();
                
            if (fileName.equalsIgnoreCase(fileToSearch)) {
                results.put(fileName, path);
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + path);
        }
    }
}

