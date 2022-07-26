package tests;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.HttpTaskManager;
import manager.LocalDateTimeAdapter;
import org.junit.jupiter.api.*;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class HttpTaskServerTest {
    static KVServer kvServer;
    static HttpTaskServer httpTaskServer;
    static HttpClient httpClient;
    static HttpTaskManager httpTaskManager;
    static DateTimeFormatter dateTimeFormatter;
    static Gson gson;


    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer("http://localhost:8078", "one");
        httpTaskServer.start();
        httpClient = HttpClient.newHttpClient();
        httpTaskManager = httpTaskServer.getManager();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void postCreateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача создана", response.body());
    }

    @Test
    void postCreateEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача создана", response.body());
    }

    @Test
    void postCreateSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic1))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Подзадача создана", response.body());
    }

    @Test
    void postUpdateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task(1,"Приготовить обед", "Сделать плов", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача обновлена", response2.body());
    }

    @Test
    void postUpdateEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic2 = new Epic(1,"Сделать завтрак", "Приготовить амлет", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача обновлена", response2.body());
    }

    @Test
    void postUpdateSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic1))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskForEpic2 = new SubTask(1,"Сделать геометрию", "Выучить теорему Пифагора", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals("Подзадача обновлена", response2.body());
    }

    @Test
    void getTasksTest() throws IOException, InterruptedException {
        Task task = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Сделать алгебру", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        HashMap<Integer, Task> mapFromServer = gson.fromJson(response3.body(), new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        assertEquals(httpTaskManager.getAllTask(), mapFromServer);
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic2 = new Epic("Сделать завтрак", "Приготовить амлет", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        HashMap<Integer, Epic> mapFromServer = gson.fromJson(response3.body(), new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());
        assertEquals(httpTaskManager.getAllEpic(), mapFromServer);
    }

    @Test
    void getSubTasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic1))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskForEpic2 = new SubTask("Сделать геометрию", "Выучить теорему Пифагора", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        HashMap<Integer, SubTask> mapFromServer = gson.fromJson(response3.body(), new TypeToken<HashMap<Integer, SubTask>>() {
        }.getType());
        assertEquals(httpTaskManager.getAllSubTask(), mapFromServer);
    }

    @Test
    void getTaskByIDTest() throws IOException, InterruptedException {
        Task task = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Сделать алгебру", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?id=1"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        Task taskFromServer = gson.fromJson(response3.body(), Task.class);
        assertEquals(httpTaskManager.getTask(1), taskFromServer);
    }

    @Test
    void getEpicByIDTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic2 = new Epic("Сделать завтрак", "Приготовить амлет", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?id=1"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        Epic epicFromServer = gson.fromJson(response3.body(), Epic.class);
        assertEquals(httpTaskManager.getEpic(1), epicFromServer);
    }

    @Test
    void getSubTaskByIDTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic1))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskForEpic2 = new SubTask("Сделать геометрию", "Выучить теорему Пифагора", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?id=2"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskFromServer = gson.fromJson(response3.body(), SubTask.class);
        assertEquals(httpTaskManager.getSubTask(2), subTaskFromServer);
    }

    @Test
    void deleteTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Сделать алгебру", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .DELETE()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals("Задачи удалены", response3.body());
    }

    @Test
    void deleteEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic2 = new Epic("Сделать завтрак", "Приготовить амлет", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .DELETE()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals("Задачи удалены", response3.body());
    }

    @Test
    void deleteSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic1))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskForEpic2 = new SubTask("Сделать геометрию", "Выучить теорему Пифагора", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .DELETE()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals("Подзадачи удалены", response3.body());
    }

    @Test
    void deleteTaskByIDTest() throws IOException, InterruptedException {
        Task task = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Сделать алгебру", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?id=1"))
                .DELETE()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals("Задача удалена", response3.body());
    }

    @Test
    void deleteEpicByIDTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic2 = new Epic("Сделать завтрак", "Приготовить амлет", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(epic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?id=1"))
                .DELETE()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals("Задача удалена", response3.body());
    }

    @Test
    void deleteSubTaskByIDTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic1))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskForEpic2 = new SubTask("Сделать геометрию", "Выучить теорему Пифагора", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(subTaskForEpic2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?id=2"))
                .DELETE()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals("Подзадача удалена", response3.body());
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        Task task = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Сделать алгебру", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task2))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?id=1"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history"))
                .GET()
                .build();
        HttpResponse<String> response4 = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        List<Task> mapFromServer = gson.fromJson(response4.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(httpTaskManager.getHistory(), mapFromServer);
    }

    @Test
    void getPrioritizedTasksHandlerTest() throws IOException, InterruptedException {
        Task task = new Task("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task))))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Task task22 = new Task("Сделать алгебру", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2002, 1, 1, 1, 1), 60);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString((gson.toJson(task22))))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> response4 = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

        List<Task> mapFromServer = gson.fromJson(response4.body(), new TypeToken<List<Task>>() {}.getType());
        Comparator<Task> startTimeСomparator = (t1, t2) -> {
            if (t1.getStartTime() == null && t2.getStartTime() == null) {
                return t1.getId() - t2.getId();
            } else if (t1.getStartTime() == null) {
                return -1;
            } else if (t2.getStartTime() == null) {
                return 1;
            } else {
                return t1.getStartTime().compareTo(t2.getStartTime());
            }
        };
        TreeSet<Task> mapFromServerTreeSet = new TreeSet<>(startTimeСomparator);
        mapFromServerTreeSet.addAll(mapFromServer);
        assertEquals(httpTaskManager.getPrioritizedTasks(), mapFromServerTreeSet);
    }

}
