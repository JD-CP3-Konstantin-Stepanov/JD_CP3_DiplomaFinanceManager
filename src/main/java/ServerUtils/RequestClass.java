package ServerUtils;

import ServerClient.ClientRequest;
import UtilClasses.RequestCollection;

import java.util.Collection;
public class RequestClass {
    public static UtilClasses.RequestClass requestClassInit(ClientRequest clientRequest) {
        String[] date = clientRequest.getDate().split("\\.");

        return new UtilClasses.RequestClass(clientRequest.getTitle(), clientRequest.getDate(), clientRequest.getSum(),
                date[0], date[1], date[2]);
    }

    public static RequestCollection RequestCollectionInit(UtilClasses.RequestClass requestClass,
                                                           Collection<UtilClasses.RequestClass> requestCollect) {
        requestCollect.add(requestClass);
        return new RequestCollection(requestCollect);
    }
}
