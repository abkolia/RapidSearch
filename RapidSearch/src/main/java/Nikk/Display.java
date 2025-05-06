package Nikk;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Desktop;

public class Display {

    private static Map<Integer, String> roots = Search.searchDisks();
    private static String choosenDisk;

    public void welcomeScreen(){
        System.out.println("Добро пожаловать в Rapid Search!");

        System.out.println("Список доступных для поиска дисков: ");
        displayDisk();

        System.out.print("Введите номер диска для поиска: ");
        choosenDisk = Disk.matchDisk();
        System.out.println();
        
        System.out.printf("Вы выбрали диск %s!\n", choosenDisk);

        System.out.print("Введите название файла/директории, которые нужно найти: ");
        String choosenFile = Disk.chooseFile();
        System.out.println();

        System.out.println("Начинаем поиск...");
        System.out.println("Найденные совпадения:");
        displayResults(choosenFile); 

        
    }

    private void displayDisk(){

        for(Map.Entry<Integer, String> root : roots.entrySet()){
            System.out.printf("%d. %s. \n", root.getKey(), root.getValue());
        }

    }

    public static String getDisk(){
        String diskLetter = choosenDisk.replaceAll(".*\\((\\w:)\\).*", "$1");
        return diskLetter;
    }

    private void displayResults(String choosenFile){
        Search search = new Search();
        int counter = 1;
        ConcurrentHashMap<String, Path> map = search.searchFile(choosenFile);
        for (Map.Entry<String, Path> entry : map.entrySet()) {
            System.out.printf("%d. Название файла: %s, Путь: %s\n", counter, entry.getKey(), entry.getValue());
            counter++;
        }
    }

    public static Map<Integer,String> getRootMap(){
        return roots;
    }
}
