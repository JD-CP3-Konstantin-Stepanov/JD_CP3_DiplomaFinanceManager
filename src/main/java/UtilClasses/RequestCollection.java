package UtilClasses;

import java.io.*;
import java.util.Collection;

public class RequestCollection implements Serializable {
    protected Collection<RequestClass> requestCollection;

    public RequestCollection(Collection<RequestClass> requestCollection) {
        this.requestCollection = requestCollection;
    }

    public Collection<RequestClass> getRequestCollection() {
        return requestCollection;
    }

    public void saveBin(File binFile) {
        RequestCollection requestCollection = new RequestCollection(this.requestCollection);
        try (FileOutputStream fos = new FileOutputStream(binFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(requestCollection);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static RequestCollection loadFromBinFile(File binFile) {
        RequestCollection requestCollection = null;
        try (FileInputStream fis = new FileInputStream(binFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            requestCollection = (RequestCollection) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return requestCollection;
    }

    public String toString() {
        return requestCollection.toString();
    }
}
