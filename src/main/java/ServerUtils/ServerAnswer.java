package ServerUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class ServerAnswer {
    public static String jsonServerResult(Map<String, Object> categoriesMap) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(categoriesMap);
    }
}
