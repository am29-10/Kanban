package manager;

import exception.ManagerSaveException;
import tasks.*;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private File file;
    private static final String HEAD = "id, type, name, status, description, dateTime, duration, epic";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public FileBackedTasksManager() {
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            bufferedWriter.write(HEAD);
            bufferedWriter.newLine();

            for (Task task : getAllTask().values()) {
                bufferedWriter.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpic().values()) {
                bufferedWriter.write(toString(epic) + "\n");
            }

            for (SubTask subTask : getAllSubTask().values()) {
                bufferedWriter.write(toString(subTask) + "\n");
            }

            bufferedWriter.write("\n");

            bufferedWriter.write(historyToString(history));
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при записи файла");
        }
    }

    public String toString(Task task) {
        String epicId = "";
        if (task != null) {
            if (task.getType() == TypeTasks.SUBTASK) {
                epicId = String.valueOf(((SubTask) task).getEpicId());
            }
            return String.format("%s, %s, %s, %s, %s, %s, %s, %s", task.getId(), task.getType(), task.getTitle(),
                    task.getStatus(), task.getDescription(), task.getStartTime().format(formatter), task.getDuration(), epicId);
        }
        return null;
    }

    public static Task fromString(String value) {
        Task task = null;
        if (value != null && !value.trim().isEmpty()) {
            String[] values = value.split(", ");

            int id = Integer.parseInt(values[0]);
            TypeTasks type = TypeTasks.valueOf(values[1]);
            String title = values[2];
            TaskStatus status = TaskStatus.valueOf(values[3]);
            String description = values[4];
            LocalDateTime startTime = LocalDateTime.parse(values[5], formatter);
            long duration = Long.parseLong(values[6]);


            switch (type) {
                case TASK:
                    task = new Task(title, description, status, startTime, duration);
                    task.setId(id);
                    break;
                case EPIC:
                    task = new Epic(title, description, status, startTime, duration);
                    task.setId(id);
                    break;
                case SUBTASK:
                    task = new SubTask(title, description, status, startTime, duration, Integer.parseInt(values[7]));
                    task.setId(id);
                    break;
            }
        }
        return task;
    }

    public String historyToString(HistoryManager manager) {
        String idLineHistory;
        List<String> idsHistory = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            idsHistory.add(String.valueOf(task.getId()));
        }
        idLineHistory = String.join(", ", idsHistory);
        return idLineHistory;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> idsHistory = new ArrayList<>();
        if (value != null && !value.trim().isEmpty()) {
            String[] values = value.split(", ");

            for (String line: values) {
                idsHistory.add(Integer.parseInt(line));
            }
        }
        return idsHistory;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        Map<Integer, Task> allTasks = new HashMap<>();
        int maxLoadedId = 0;

        List<String> list = null;
        try {
            list = Files.readAllLines(file.toPath());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if (list == null || list.isEmpty()) {
            return manager;
        }

        for (int i = 1; i < list.size(); i++) {
            String item = list.get(i);
            if (item.isBlank()) {
                break;
            }
            Task task = fromString(item);
            if (task != null) {
                if (task.getId() > maxLoadedId) {
                    maxLoadedId = task.getId();
                }
                allTasks.put(task.getId(), task);
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        manager.epics.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        SubTask subTask = (SubTask) task;
                        Epic epicOfSubTask = manager.epics.get(subTask.getEpicId());
                        manager.subTasks.put(subTask.getId(), subTask);
                        epicOfSubTask.getSubTasksId().add(subTask.getId());
                        manager.updateEpic(epicOfSubTask);
                        break;
                }
            }
        }

        if (list.size() > 1) {
            String history = list.get(list.size() - 1);
            List<Integer> ids = historyFromString(history);
            Collections.reverse(ids);
            for (Integer id : ids) {
                manager.history.addHistory(allTasks.get(id));
            }
        }
        manager.id = maxLoadedId;
        return manager;
    }

    public Task taskFromRequest(String body) {
        String title = null;
        String description = null;
        int id = 0;
        TaskStatus status = null;
        long duration = 0;
        LocalDateTime startTime = null;

        String[] newString = body
                .replaceFirst("\\{\\s*", "")
                .replaceAll("\"", "")
                .replaceFirst("\\s*}", "")
                .split(",\\s*");

        for (String string : newString) {
            String[] keyValueString = string.split(": ");
            String key = keyValueString[0];
            String value = keyValueString[1];

            switch (key) {
                case "title":
                    title = value;
                    continue;
                case "description":
                    description = value;
                    continue;
                case "id":
                    id = Integer.parseInt(value);
                    continue;
                case "status":
                    status = TaskStatus.valueOf(value);
                    continue;
                case "startTime":
                    startTime = LocalDateTime.parse(value, formatter);
                    continue;
                case "duration":
                    duration = Long.parseLong(value);
                    continue;
                default:
                    break;
            }
        }
        return new Task(id, title, description, status, startTime, duration);
    }

    public Epic epicFromRequest(String body) {
        List<Integer> subTasksIdArray = new ArrayList<>();
        String title = null;
        String description = null;
        int id = 0;
        TaskStatus status = null;
        long duration = 0;
        LocalDateTime startTime = null;

        String[] newString = body
                .replaceFirst("\\{\\s*", "")
                .replaceFirst("\\s*}", "")
                .split(",\\s*\"");

        for (String string : newString) {
            String[] keyValueString = string.split(": ");
            String key = keyValueString[0]
                    .replaceAll("\"", "");
            String value = keyValueString[1]
                    .replaceAll("\"", "");

            switch (key) {
                case "subTasksId":
                    if (!value.equals("[]")) {
                        String[] stringArrayValue = value
                                .replaceFirst("\\[", "")
                                .replaceAll("\\s*", "")
                                .replaceFirst("]", "")
                                .split(",");
                        for (String idSubTask : stringArrayValue) {
                            subTasksIdArray.add(Integer.parseInt(idSubTask));
                        }
                    }
                    continue;
                case "title":
                    title = value;
                    continue;
                case "description":
                    description = value;
                    continue;
                case "id":
                    id = Integer.parseInt(value);
                    continue;
                case "status":
                    status = TaskStatus.valueOf(value);
                    continue;
                case "startTime":
                    if (subTasksIdArray.size() != 0) {
                        startTime = getterEpicStartTime(subTasksIdArray);
                    } else {
                        startTime = LocalDateTime.parse(value, formatter);
                    }
                    continue;
                case "duration":
                    if (subTasksIdArray.size() != 0) {
                        duration = getEpicDuration(subTasksIdArray);
                    } else {
                        duration = Long.parseLong(value);
                    }
                    continue;
                default:
                    break;
            }
        }
        return new Epic(id, title, description, status, startTime, duration);
    }

    public SubTask subTaskFromRequest(String body) {
        String title = null;
        String description = null;
        int id = 0;
        TaskStatus status = null;
        long duration = 0;
        LocalDateTime startTime = null;
        int epicId = 0;

        String[] newString = body
                .replaceFirst("\\{\\s*", "")
                .replaceAll("\"", "")
                .replaceFirst("\\s*}", "")
                .split(",\\s*");

        for (String string : newString) {
            String[] keyValueString = string.split(": ");
            String key = keyValueString[0];
            String value = keyValueString[1];

            switch (key) {
                case "title":
                    title = value;
                    continue;
                case "description":
                    description = value;
                    continue;
                case "id":
                    id = Integer.parseInt(value);
                    continue;
                case "status":
                    status = TaskStatus.valueOf(value);
                    continue;
                case "startTime":
                    startTime = LocalDateTime.parse(value, formatter);
                    continue;
                case "duration":
                    duration = Long.parseLong(value);
                    continue;
                case "epicId":
                    epicId = Integer.parseInt(value);
                    continue;
                default:
                    break;
            }
        }
        return new SubTask(id, title, description, status, startTime, duration, epicId);
    }

    @Override
    public Task createTask(Task task) { // Создание задачи конкретного типа
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
        return subTask;
    }


    @Override
    public Task getTask(int id) { // Получение задачи по ее id номеру
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int idSubTask) {
        SubTask subTask = super.getSubTask(idSubTask);
        save();
        return subTask;
    }

    @Override
    public Map<Integer, Task> getAllTask() { // Получение массива всех задач для конкретного типа задачи
        return super.getAllTask();
    }

    @Override
    public Map<Integer, Epic> getAllEpic() {
        return super.getAllEpic();
    }

    @Override
    public Map<Integer, SubTask> getAllSubTask() {
        return super.getAllSubTask();
    }

    @Override
    public void clearTasks() { // Удаление всех задач
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }

    @Override
    public void clearSubTask() {
        super.clearSubTask();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public Task updateTask(Task task) { // Обновление всех типов задач
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public LocalDateTime getterEpicStartTime(List<Integer> listOfSubTaskId) {
        return super.getEpicStartTime(listOfSubTaskId);
    }

}
