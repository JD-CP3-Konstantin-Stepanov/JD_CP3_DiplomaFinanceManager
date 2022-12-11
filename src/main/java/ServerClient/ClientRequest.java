package ServerClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientRequest {
    protected String title;
    protected String date;
    protected Integer sum;

    public ClientRequest(String title, String date, Integer sum) {
        this.title = title;
        this.date = date;
        this.sum = sum;
    }

    public static void main(String[] args) {

        try (Socket socket = new Socket("127.0.0.1", 8989);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            String serverMessage = reader.readLine();
            System.out.println(serverMessage);

            File jsonFile = new File("clientRequest.json");

            System.out.println("Введите название продукта:");
            String title_in = scanner.nextLine();

            System.out.println("Введите дату покупки в формате ГОД.МЕСЯЦ.ДЕНЬ:");
            String date_in = scanner.nextLine();
            String[] dateArray = date_in.split("\\.");
            if (dateArray.length != 3) {
                throw new RuntimeException("Необходимо ввести дату в формате ГОД.МЕСЯЦ.ДЕНЬ!");
            } else if (dateArray[0].length() != 4) {
                throw new RuntimeException("Некорректно задано значение года!");
            } else if (Integer.parseInt(dateArray[1]) < 1 || Integer.parseInt(dateArray[1]) > 12) {
                throw new RuntimeException("Некорректно задано значение месяца!");
            } else if (Integer.parseInt(dateArray[2]) < 1 || Integer.parseInt(dateArray[2]) > 31) {
                throw new RuntimeException("Некорректно задано значение дня!");
            }

            System.out.println("Введите цену покупки:");
            Integer sum_in = Integer.parseInt(scanner.nextLine());

            formatJson(jsonFile, title_in, date_in, sum_in);
            writer.println(jsonFile);
            writer.flush();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public static void formatJson(File jsonFile, String title, String date, Integer sum) {
        ClientRequest gsonClientRequest = new ClientRequest(title, date, sum);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        gson.toJson(gsonClientRequest);

        try (FileWriter file = new FileWriter(jsonFile)) {
            file.write(gson.toJson(gsonClientRequest));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public Integer getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return title + " " + date + " " + sum;
    }
}