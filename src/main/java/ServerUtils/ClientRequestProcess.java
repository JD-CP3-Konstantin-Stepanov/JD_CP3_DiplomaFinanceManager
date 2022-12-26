package ServerUtils;

import ServerClient.ClientRequest;
import UtilClasses.RequestClass;

import java.util.Map;

public class ClientRequestProcess {
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
}
