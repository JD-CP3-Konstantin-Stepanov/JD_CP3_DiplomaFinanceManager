package ServerClient;

import ServerUtils.ProcessClientRequest;
import UtilClasses.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static ServerUtils.ClientRequestProcess.mapSumForm;
import static ServerUtils.ReadCategories.readCategories;
import static ServerUtils.RequestClass.RequestCollectionInit;
import static ServerUtils.RequestClass.requestClassInit;
import static ServerUtils.ServerAnswer.jsonServerResult;
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
                try (Socket clientSrv = server.accept();
                     PrintWriter writer = new PrintWriter(clientSrv.getOutputStream(), true);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSrv.getInputStream()))) {

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

                    ClientRequest clientRequest = ProcessClientRequest.processClientRequest(jsonRequest);

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
}