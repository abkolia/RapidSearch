package Nikk;

import java.util.Map;
import java.util.Scanner;

public class Disk {
    private static Scanner scanner = new Scanner(System.in);

    protected static int chooseDisk(){
        while(!scanner.hasNextInt()){
            System.out.println("Неправильный ввод!");
            scanner.next();
        } 
        
        return scanner.nextInt();
    } 

    protected static String chooseFile(){
            while(!scanner.hasNext()){
                System.out.println("Неправильный ввод!");
                scanner.nextLine();
            } 
            return scanner.next();
    }

    protected static String matchDisk(){
        int input = chooseDisk();
        Map<Integer, String> roots = Display.getRootMap();

        while(!roots.containsKey(input)){
            System.out.println("Вы ввели несуществующий номер диска!");
            System.out.print("Введите номер диска для поиска: ");
            input = chooseDisk();
        }
        return roots.get(input);
    }
}
