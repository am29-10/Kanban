import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.*;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Main {
    private final static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws IOException, InterruptedException {
        //FileBackedTasksManager manager1 = new FileBackedTasksManager(new File("Test.csv"));
        //new KVServer().start();

        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer("http://localhost:8078", "one");
        httpTaskServer.start();
        HttpTaskManager manager = httpTaskServer.getManager();
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        manager.createEpic(epic);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        manager.createSubTask(subTaskForEpic1);
        SubTask subTaskForEpic2 = new SubTask("Сделать геометрию", "Конспект по теореме Пифагора", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60, 1);
        manager.createSubTask(subTaskForEpic2);
        SubTask subTaskForEpic3 = new SubTask("Сделать физику", "Конспект по закону Ома", TaskStatus.NEW, LocalDateTime.of(2022, 4, 4, 4, 4), 60, 1);
        manager.createSubTask(subTaskForEpic3);
        Task task = new Task("Сделать уроки 22", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        manager.createTask(task);
        Task task2 = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2003, 1, 1, 1, 1), 60);
        manager.createTask(task2);

        Epic epic1 = new Epic("Прочитать книжку", "Чтение в библиотеке", TaskStatus.NEW, LocalDateTime.of(2022, 5, 5, 5, 5), 60);
        manager.createEpic(epic1);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Ответ в формате JSON: " + response.body());
        HashMap<Integer, Task> mapFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        System.out.println(mapFromServer);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?id=5"))
                .DELETE()
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        System.out.println("Ответ в формате JSON: " + response2.body());
        System.out.println(response2);

        // Создаю задачу
        Task task3 = new Task("Сделать уроки36", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);

        /*String bodyFromInsomnia = " {\n" +
                "\t\t\"title\": \"Сделать уроки\",\n" +
                "\t\t\"description\": \"Написать конспекты\",\n" +
                "\t\t\"id\": 6,\n" +
                "\t\t\"status\": \"NEW\",\n" +
                "\t\t\"type\": \"TASK\",\n" +
                "\t\t\"duration\": 60,\n" +
                "\t\t\"startTime\": \"01.01.2003 01:01\",\n" +
                "\t}";*/
        //HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(bodyFromInsomnia));
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task3))))
                .build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        System.out.println("Ответ в формате JSON: " + response3.body());

        //kvServer.stop();
        //httpTaskServer.stop();


    }
}





