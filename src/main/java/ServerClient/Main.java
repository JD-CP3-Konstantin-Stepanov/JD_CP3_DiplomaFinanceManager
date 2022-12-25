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

            RequestClass requestClass;
            Collection<RequestClass> requestCollect = new ArrayList<>();
            RequestCollection requestCollection;

            File binFile = new File("RequestCollection.bin");
            if (binFile.exists()) {
                requestCollection = RequestCollection.loadFromBinFile(binFile);
                Collection<RequestClass> requestList = requestCollection.getRequestCollection();

                for (RequestClass rq : requestList) {
                    ClientRequest clientRequest = new ClientRequest(rq.getRequestTitle(), rq.getRequestDate(),
                            rq.getRequestSum());
                    mapSumForm(clientRequest, mapSum, categoriesMap);
                    requestClass = requestClassInit(clientRequest);
                    requestCollection = RequestCollectionInit(requestClass, requestCollect);
                }
                //DEBUG
                System.out.println("TOTAL SUM: " + mapSum);
                //System.out.println("LIST: " + requestCollection);
                //DEBUG
            }

            while (true) {
                try (Socket client_srv = server.accept();
                     PrintWriter writer = new PrintWriter(client_srv.getOutputStream(), true);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(client_srv.getInputStream()))) {

                    writer.println("New connection accepted!");
                    writer.flush();

                    String jsonRequest = reader.readLine();
                    if (jsonRequest == null || jsonRequest.equals("")) {
                        continue;
                    } else {
                        jsonRequest = jsonRequest.replace("\t", "\n");
                        System.out.println("-------------------------------------------------------------------------");
                        System.out.println("Client request:");
                        System.out.println(jsonRequest);
                        System.out.println("-------------------------------------------------------------------------");
                    }

                    ClientRequest clientRequest = processClientRequest(jsonRequest);

                    requestClass = requestClassInit(clientRequest);
                    requestCollection = RequestCollectionInit(requestClass, requestCollect);

                    requestCollection.saveBin(binFile);
                    mapSumForm(clientRequest, mapSum, categoriesMap);
                    //DEBUG
                    //System.out.println("LIST: " + requestCollection);
                    //System.out.println("TOTAL SUM: " + mapSum);
                    //DEBUG
                    //Формирование maxCategory
                    MaxCategory maxCategory = maxCategorySearch(mapSum);
                    mapSumGson.put("maxCategory", maxCategory);
                    //Формирование maxYearCategory
                    maxYearCategory maxYearCategory = UtilClasses.maxYearCategory
                            .maxYearCategorySearch(requestCollection, categoriesMap);
                    mapSumGson.put("maxYearCategory", maxYearCategory);
                    //Формирование maxMonthCategory
                    maxMonthCategory maxMonthCategory = UtilClasses.maxMonthCategory
                            .maxMonthCategorySearch(requestCollection, categoriesMap);
                    mapSumGson.put("maxMonthCategory", maxMonthCategory);
                    //Формирование maxDayCategory
                    maxDayCategory maxDayCategory = UtilClasses.maxDayCategory
                            .maxDayCategorySearch(requestCollection, categoriesMap);
                    mapSumGson.put("maxDayCategory", maxDayCategory);

                    //Вывод результата работы сервера в Json
                    writer.print(jsonServerResult(mapSumGson));
                    writer.flush();
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

    private static ClientRequest processClientRequest(String jsonFile) {
        JSONParser parser = new JSONParser();
        ClientRequest clientRequest;
        try {
            Object obj = parser.parse(jsonFile);
            JSONObject ParsedJson = (JSONObject) obj;

            String titleJson = (String) ParsedJson.get("title");
            String dateJson = (String) ParsedJson.get("date");
            Integer sumJson = Integer.valueOf(ParsedJson.get("sum").toString());

            clientRequest = new ClientRequest(titleJson, dateJson, sumJson);
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

    public static String titleSearchMaxCategories(RequestClass requestClass, Map<String, String> categoriesMap) {
        if (categoriesMap.get(requestClass.getRequestTitle()) == null) {
            return "другое";
        } else {
            return categoriesMap.get(requestClass.getRequestTitle());
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

    public static String jsonServerResult(Map<String, Object> categoriesMap) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(categoriesMap);
    }

    private static RequestClass requestClassInit(ClientRequest clientRequest) {
        String[] date = clientRequest.getDate().split("\\.");

        return new RequestClass(clientRequest.getTitle(), clientRequest.getDate(), clientRequest.getSum(),
                date[0], date[1], date[2]);
    }

    private static RequestCollection RequestCollectionInit(RequestClass requestClass,
                                                           Collection<RequestClass> requestCollect) {
        requestCollect.add(requestClass);
        return new RequestCollection(requestCollect);
    }

}