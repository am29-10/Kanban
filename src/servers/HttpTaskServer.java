package servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpTaskServer {
    private static HttpTaskManager manager;
    private static HttpServer httpServer;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson;

    public HttpTaskServer(String url, String keyForSave) throws  IOException {
        manager = Managers.getDefaultManager(url, keyForSave);
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/task?id={id}", new TaskHandler());

        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/epic?id={id}", new EpicHandler());

        httpServer.createContext("/tasks/subtask", new SubTaskHandler());
        httpServer.createContext("/tasks/subtask?id={id}", new SubTaskHandler());
        httpServer.createContext("/tasks/subtask/epic?id={id}", new SubTaskHandler());

        httpServer.createContext("/tasks/history", new HistoryHandler());

        httpServer.createContext("/tasks", new PrioritizedTasksHandler());
    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = String.valueOf(httpExchange.getRequestURI());
            String body;
            String response;
            String taskGson;
            System.out.println(httpExchange.getRequestURI());
            System.out.println("Началась обработка метода " + method + " запроса " + path + " от клиента.");

            switch (method) {
                case "GET":
                    if (path.endsWith("/tasks/task")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            taskGson = gson.toJson(manager.getAllTask());
                            os.write(taskGson.getBytes(DEFAULT_CHARSET));
                        }
                    } else if (path.startsWith("/tasks/task?id=")) {
                        int id = Integer.parseInt(path.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            taskGson = gson.toJson(manager.getTask(id));
                            os.write(taskGson.getBytes(DEFAULT_CHARSET));
                        }
                    }
                    break;
                case "POST":
                    if (path.endsWith("/tasks/task")) {
                        httpExchange.sendResponseHeaders(201, 0);
                        try (InputStream is = httpExchange.getRequestBody()) {
                            body = new String(is.readAllBytes(), DEFAULT_CHARSET);
                            Task task = manager.taskFromRequest(body);

                            if (!manager.getAllTask().containsKey(task.getId())) {
                                manager.createTask(task);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    response = "Задача создана";
                                    os.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else {
                                manager.updateTask(task);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    response = "Задача обновлена";
                                    os.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            }
                        }
                    }
                    break;
                case "DELETE":
                    if (path.endsWith("/tasks/task")) {
                        if (!manager.getAllTask().isEmpty()) {
                            httpExchange.sendResponseHeaders(202, 0);
                            manager.clearTasks();
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Задачи удалены";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else {
                            httpExchange.sendResponseHeaders(202, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Список задач пуст, удалять нечего";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                    } else if (path.startsWith("/tasks/task?id=")) {
                        int id = Integer.parseInt(path.split("=")[1]);
                        if (manager.getAllTask().containsKey(id)) {
                            httpExchange.sendResponseHeaders(202, 0);
                            manager.removeTask(id);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Задача удалена";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else {
                            httpExchange.sendResponseHeaders(202, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Задачи с таким номером id нет";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(202, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        response = "Введен несуществующий метод для данного сервера";
                        os.write(response.getBytes(DEFAULT_CHARSET));
                    }
            }
        }
    }

    static class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = String.valueOf(httpExchange.getRequestURI());
            String body;
            String response;
            String epicGson;
            System.out.println(httpExchange.getRequestURI());
            System.out.println("Началась обработка метода " + method + " запроса " + path + " от клиента.");

            switch (method) {
                case "GET":
                    if (path.endsWith("/tasks/epic")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            epicGson = gson.toJson(manager.getAllEpic());
                            os.write(epicGson.getBytes(DEFAULT_CHARSET));
                        }
                    } else if (path.startsWith("/tasks/epic?id=")) {
                        int id = Integer.parseInt(path.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            epicGson = gson.toJson(manager.getEpic(id));
                            os.write(epicGson.getBytes(DEFAULT_CHARSET));
                        }
                    }
                    break;
                case "POST":
                    if (path.endsWith("/tasks/epic")) {
                        try (InputStream is = httpExchange.getRequestBody()) {
                            body = new String(is.readAllBytes(), DEFAULT_CHARSET);
                            Epic epic = manager.epicFromRequest(body);

                            if (!manager.getAllEpic().containsKey(epic.getId())) {
                                httpExchange.sendResponseHeaders(201, 0);
                                manager.createEpic(epic);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    response = "Задача создана";
                                    os.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else {
                                httpExchange.sendResponseHeaders(201, 0);
                                manager.updateEpic(epic);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    response = "Задача обновлена";
                                    os.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            }
                        }
                    }
                    break;
                case "DELETE":
                    if (path.endsWith("/tasks/epic")) {
                        if (!manager.getAllEpic().isEmpty()) {
                            httpExchange.sendResponseHeaders(202, 0);
                            manager.clearEpic();
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Задачи удалены";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else {
                            httpExchange.sendResponseHeaders(202, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Список задач пуст, удалять нечего";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                    } else if (path.startsWith("/tasks/epic?id=")) {
                        int id = Integer.parseInt(path.split("=")[1]);
                        if (manager.getAllEpic().containsKey(id)) {
                            httpExchange.sendResponseHeaders(202, 0);
                            manager.removeEpic(id);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Задача удалена";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else {
                            httpExchange.sendResponseHeaders(202, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Задачи с таким номером id нет";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(202, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        response = "Введен несуществующий метод для данного сервера";
                        os.write(response.getBytes(DEFAULT_CHARSET));
                    }
            }
        }
    }

    static class SubTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = String.valueOf(httpExchange.getRequestURI());
            System.out.println(httpExchange.getRequestURI());
            List<SubTask> subTasks = new ArrayList<>();
            String body;
            String response;
            String subTaskGson;
            System.out.println("Началась обработка метода " + method + " запроса " + path + " от клиента.");

            switch (method) {
                case "GET":
                    if (path.endsWith("/tasks/subtask")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            subTaskGson = gson.toJson(manager.getAllSubTask());
                            os.write(subTaskGson.getBytes(DEFAULT_CHARSET));
                        }
                    } else if (path.startsWith("/tasks/subtask?id=")) {
                        int id = Integer.parseInt(path.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            subTaskGson = gson.toJson(manager.getSubTask(id));
                            os.write(subTaskGson.getBytes(DEFAULT_CHARSET));
                        }
                    } else if (path.startsWith("/tasks/subtask/epic?id=")) {
                        int id = Integer.parseInt(path.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        for (Integer subTaskId : manager.getSubTaskForEpic(id)) {
                            subTasks.add(manager.getSubTask(subTaskId));
                        }
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            String subTaskForEpicGson = gson.toJson(subTasks);
                            os.write(subTaskForEpicGson.getBytes(DEFAULT_CHARSET));
                        }
                    }
                    break;
                case "POST":
                    if (path.endsWith("/tasks/subtask")) {
                        try (InputStream is = httpExchange.getRequestBody()) {
                            body = new String(is.readAllBytes(), DEFAULT_CHARSET);
                            SubTask subTask = manager.subTaskFromRequest(body);

                            if (!manager.getAllSubTask().containsKey(subTask.getId())) {
                                httpExchange.sendResponseHeaders(201, 0);
                                manager.createSubTask(subTask);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    response = "Подзадача создана";
                                    os.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            } else {
                                httpExchange.sendResponseHeaders(201, 0);
                                manager.updateSubTask(subTask);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    response = "Подзадача обновлена";
                                    os.write(response.getBytes(DEFAULT_CHARSET));
                                }
                            }
                        }
                    }
                    break;
                case "DELETE":
                    if (path.endsWith("/tasks/subtask")) {
                        if (!manager.getAllSubTask().isEmpty()) {
                            httpExchange.sendResponseHeaders(202, 0);
                            manager.clearSubTask();
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Подзадачи удалены";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else {
                            httpExchange.sendResponseHeaders(202, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Список подзадач пуст, удалять нечего";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                    } else if (path.startsWith("/tasks/subtask?id=")) {
                        int id = Integer.parseInt(path.split("=")[1]);
                        if (manager.getAllSubTask().containsKey(id)) {
                            httpExchange.sendResponseHeaders(202, 0);
                            manager.removeSubTask(id);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Подзадача удалена";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        } else {
                            httpExchange.sendResponseHeaders(202, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                response = "Подзадачи с таким номером id нет";
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(202, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        response = "Введен несуществующий метод для данного сервера";
                        os.write(response.getBytes(DEFAULT_CHARSET));
                    }
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = String.valueOf(httpExchange.getRequestURI());
            String response;
            String historyGson;
            System.out.println(httpExchange.getRequestURI());
            System.out.println("Началась обработка метода " + method + " запроса " + path + " от клиента.");

            switch (method) {
                case "GET":
                    if (path.endsWith("/tasks/history")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            historyGson = gson.toJson(manager.getHistory());
                            os.write(historyGson.getBytes(DEFAULT_CHARSET));
                        }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(202, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        response = "Введен несуществующий метод для данного сервера";
                        os.write(response.getBytes(DEFAULT_CHARSET));
                    }
            }
        }
    }

    static class PrioritizedTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = String.valueOf(httpExchange.getRequestURI());
            String response;
            String historyGson;
            System.out.println(httpExchange.getRequestURI());
            System.out.println("Началась обработка метода " + method + " запроса " + path + " от клиента.");

            switch (method) {
                case "GET":
                    if (path.endsWith("/tasks")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            historyGson = gson.toJson(manager.getPrioritizedTasks());
                            os.write(historyGson.getBytes(DEFAULT_CHARSET));
                        }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(202, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        response = "Введен несуществующий метод для данного сервера";
                        os.write(response.getBytes(DEFAULT_CHARSET));
                    }
            }
        }
    }

    public HttpTaskManager getManager() {
        return manager;
    }

    public void start() {
        if (httpServer != null) {
            System.out.println("Запущен HttpTaskServer на порту " + PORT);
            httpServer.start();
        }
    }

    public void stop() {
        System.out.println("Остановлен HttpTaskServer на порту " + PORT);
        httpServer.stop(0);
    }

}
