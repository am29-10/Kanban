package manager;

import exception.KVServerException;
import exception.KVTaskClientException;
import servers.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.net.http.HttpResponse;

public class HttpTaskManager extends FileBackedTasksManager {
    private final String keyForSave;
    private KVTaskClient kvTaskClient;
    private static Gson gson;
    public static String HEAD = "id, type, name, status, description, dateTime, duration, epic";

    public HttpTaskManager(String url, String keyForSave) {
        this.keyForSave = keyForSave;
        try {
            kvTaskClient = new KVTaskClient(url);
        } catch (KVTaskClientException e) {
            System.out.println(e.getMessage());
        }
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        loadFromSave();
    }

    @Override
    public void save() {
        try {
            StringBuilder tasksInString = new StringBuilder();
            tasksInString.append(HEAD)
                    .append("\n");

            for (Task task : getAllTask().values()) {
                tasksInString.append(toString(task))
                        .append("\n");
            }

            for (Epic epic : getAllEpic().values()) {
                tasksInString.append(toString(epic))
                        .append("\n");
            }

            for (SubTask subTask : getAllSubTask().values()) {
                tasksInString.append(toString(subTask))
                        .append("\n");
            }
            tasksInString.append(historyToString(history));
            String string = tasksInString.toString();
            HttpResponse<String> response = kvTaskClient.put(keyForSave, string);
            if (response == null) {
                throw new KVServerException("Code 500. Ошибка сохранения!");
            }
            int responseStatus = response.statusCode();
            switch (responseStatus) {
                case 200:
                    break;
                case 400:
                    throw new KVServerException("Code 400. Ошибка сохранения! " +
                            "Сервером получен запрос с пустым значением KeyForSave");
                case 401:
                    throw new KVServerException("Code 401. Ошибка сохранения! " +
                            "Сервером получен запрос без API_TOKEN");
                case 405:
                    throw new KVServerException("Code 405. Ошибка сохранения! " +
                            "Сервером получен неверный тип запроса. Ожидается POST.");
                case 500:
                    throw new KVServerException("Code 500. Ошибка сохранения! " +
                            "Ошибка открытия OutPutStream в ходе работы KVServer");
                default:
                    throw new KVServerException("Code 500. Ошибка сохранения!");
            }
        } catch (KVServerException | KVTaskClientException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadFromSave() {
        try {
            HttpResponse<String> response = kvTaskClient.load(keyForSave);
            if (response == null) {
                throw new KVServerException("Code 500. Ошибка загрузки данных!");
            }
            int responseStatus = response.statusCode();
            switch (responseStatus) {
                case 200:
                    readKVServerResponseBody(response.body());
                    break;
                case 400:
                    throw new KVServerException("Code 400. Ошибка загрузки данных! " +
                            "Сервером получен запрос с пустым значением KeyForSave");
                case 401:
                    throw new KVServerException("Code 401. Ошибка загрузки данных! " +
                            "Сервером получен запрос без API_TOKEN");
                case 404:
                    break;
                case 405:
                    throw new KVServerException("Code 405. Ошибка загрузки данных! " +
                            "Сервером получен неверный тип запроса. Ожидается GET.");
                case 500:
                    throw new KVServerException("Code 500. Ошибка загрузки данных! " +
                            "Ошибка работы OutPutStream");
                default:
                    throw new KVServerException("Code 500. Ошибка загрузки данных!");
            }
        } catch (KVServerException | KVTaskClientException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void readKVServerResponseBody(String json) {
        String value = gson.fromJson(json, String.class);
        if (value != null && (!value.isBlank())) {
            String[] lines = value.split("\n");
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].equals("")) {
                    fromString(lines[i]);
                } else {
                    historyFromString(lines[i + 1]);
                    break;
                }
            }
        }
    }
}
