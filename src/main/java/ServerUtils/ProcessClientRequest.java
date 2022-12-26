package ServerUtils;

import ServerClient.ClientRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ProcessClientRequest {
    public static ClientRequest processClientRequest(String jsonFile) {
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
}
