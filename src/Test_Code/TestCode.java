package Test_Code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestCode {

    private void tokenizeSomeData() {
        File file = new File("./src/Test_Code/TestCode.txt");

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Scanner scanner = new Scanner(fileInputStream);

            String line = scanner.nextLine();

            System.out.println(line);
            line = line.substring(1, line.length() - 1);
            String token = "\" \"";

            String[] tokens = line.split(token);

            for (String t : tokens) {

                System.out.println(t);

            }
            
            scanner.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    public static void main(String args[]) {
        new TestCode().tokenizeSomeData();;
    }
}
