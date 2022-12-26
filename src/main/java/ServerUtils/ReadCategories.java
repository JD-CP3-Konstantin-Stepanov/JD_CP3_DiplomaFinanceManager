package ServerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadCategories {
    public static Map<String, String> readCategories() {
        File tsvFile = new File("categories.tsv");
        Map<String, String> map = new HashMap<>();
        try (BufferedReader in = new BufferedReader(new FileReader(tsvFile))) {
            String read;
            while ((read = in.readLine()) != null) {
                String[] splitArray = read.split("\n");
                for (String value : splitArray) {
                    String[] splitWord = value.split("\t");
                    map.put(splitWord[0], splitWord[1]);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return map;
    }
}
