package manager;

import tasks.*;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File file;
    private static final String HEAD = "id, type, name, status, description, epic";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            bufferedWriter.write(HEAD);
            bufferedWriter.newLine();

            for (Task task : getAllTask()) {
                bufferedWriter.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpic()) {
                bufferedWriter.write(toString(epic) + "\n");
            }

            for (SubTask subTask : getAllSubTask()) {
                bufferedWriter.write(toString(subTask) + "\n");
            }

            bufferedWriter.write("\n");

            bufferedWriter.write(historyToString(history));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String toString(Task task) {
        String epicId = "";
        if (task != null) {
            if (task.getType() == TypeTasks.SUBTASK) {
                epicId = String.valueOf(((SubTask) task).getEpicId());
            }
            return String.format("%s, %s, %s, %s, %s, %s", task.getId(), task.getType(), task.getTitle(),
                    task.getStatus(), task.getDescription(), epicId);
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

            switch (type) {
                case TASK:
                    task = new Task(title, description, status);
                    task.setId(id);
                    break;
                case EPIC:
                    task = new Epic(title, description, status);
                    task.setId(id);
                    break;
                case SUBTASK:
                    task = new SubTask(title, description, status, Integer.parseInt(values[5]));
                    task.setId(id);
                    break;
            }
        }
        return task;
    }

    public static String historyToString(HistoryManager manager) {
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

    @Override
    public void createTask(Task task) { // Создание задачи конкретного типа
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
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
    public void updateTask(Task task) { // Обновление всех типов задач
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }
}
