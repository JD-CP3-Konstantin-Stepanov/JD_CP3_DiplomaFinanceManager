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

            System.out.println(reader.readLine());

            System.out.println("Введите название продукта:");
            String titleIn = scanner.nextLine();

            System.out.println("Введите дату покупки в формате ГОД.МЕСЯЦ.ДЕНЬ:");
            String dateIn = scanner.nextLine();
            String[] dateArray = dateIn.split("\\.");
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
            int sumIn;
            try {
                sumIn = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                throw new RuntimeException("Некорректное значение цены покупки!");
            }

            String jsonRequest = formatJson(titleIn, dateIn, sumIn);
            jsonRequest = jsonRequest.replace("\n", "\t");
            writer.println(jsonRequest);
            writer.flush();

            int character;
            StringBuilder jsonAnswer = new StringBuilder();
            while ((character = reader.read()) != -1) {
                jsonAnswer.append((char) character);
            }
            System.out.println("---------------------------------------------------------------------------");
            System.out.println(jsonAnswer);
            System.out.println("---------------------------------------------------------------------------");

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public static String formatJson(String title, String date, Integer sum) {
        ClientRequest gsonClientRequest = new ClientRequest(title, date, sum);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(gsonClientRequest);
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