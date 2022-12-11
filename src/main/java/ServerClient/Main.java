package ServerClient;

import UtilClasses.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static UtilClasses.MaxCategory.maxCategorySearch;

public class Main {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8989)) {
            System.out.println("Server started");
            Map<String, Integer> mapSum = new LinkedHashMap<>();
            Map<String, String> categoriesMap = readCategories();
            Map<String, Object> mapSumGson = new LinkedHashMap<>();

            while (true) {
                try (Socket client_srv = server.accept();
                     PrintWriter writer = new PrintWriter(client_srv.getOutputStream(), true);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(client_srv.getInputStream()))) {

                    writer.println("New connection accepted!");
                    String jsonFilePath = reader.readLine();
                    if (jsonFilePath == null) {
                        continue;
                    }

                    File jsonFile = new File(jsonFilePath);
                    ClientRequest clientRequest = processClientRequest(jsonFile);
                    mapSumForm(clientRequest, mapSum, categoriesMap);

                    //Формирование maxCategory
                    MaxCategory maxCategory = maxCategorySearch(mapSum);
                    mapSumGson.put("maxCategory", maxCategory);

                    //Вывод результата работы сервера в Json
                    jsonServerResult(mapSumGson);
                }
            }
        } catch (IOException e) {
            System.out.println("Server initialization error");
            e.printStackTrace();
        }
    }

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

    private static ClientRequest processClientRequest(File jsonFile) {
        JSONParser parser = new JSONParser();
        ClientRequest clientRequest = null;
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject ParsedJson = (JSONObject) obj;

            String titleJson = (String) ParsedJson.get("title");
            String dateJson = (String) ParsedJson.get("date");
            Integer sumJson = Integer.valueOf(ParsedJson.get("sum").toString());

            clientRequest = new ClientRequest(titleJson, dateJson, sumJson);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }
        return clientRequest;
    }

    public static String titleSearch(ClientRequest request, Map<String, String> categoriesMap) {
        if (categoriesMap.get(request.getTitle()) == null) {
            return "другое";
        } else {
            return categoriesMap.get(request.getTitle());
        }
    }

    public static void mapSumForm(ClientRequest request, Map<String, Integer> mapSum,
                                  Map<String, String> categoriesMap) {
        if (mapSum.get(titleSearch(request, categoriesMap)) == null) {
            mapSum.put(titleSearch(request, categoriesMap), request.getSum());
        } else {
            mapSum.merge(titleSearch(request, categoriesMap), request.getSum(), Integer::sum);
        }
    }

    public static void jsonServerResult(Map<String, Object> categoriesMap) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();

        File serverJsonResult = new File("serverJsonResult.json");
        try (FileWriter file = new FileWriter(serverJsonResult)) {
            file.write(gson.toJson(categoriesMap));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}