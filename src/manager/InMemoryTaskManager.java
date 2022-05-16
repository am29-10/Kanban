package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int idTask = 0;
    private int idEpic = 0;
    private int idSubTusk = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager history;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.history = Managers.getDefaultHistory();
    }

    @Override
    public void createTask(Task task) { // Создание задачи конкретного типа
        task.setId(++idTask);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++idEpic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
            subTask.setId(++idSubTusk);
            subTasks.put(subTask.getId(), subTask);
        if (getSubTaskForEpic(subTask.getId()) != null) {
            getSubTaskForEpic(subTask.getEpicId()).add(subTask.getId());
            updateStatusEpic(subTask.getEpicId());
        }

    }

    @Override
    public Collection<Task> getAllTask() { // Получение массива всех задач для конкретного типа задачи
        return tasks.values();
    }

    @Override
    public Collection<Epic> getAllEpic() {
        return epics.values();
    }

    @Override
    public Collection<SubTask> getAllSubTask() {
        return subTasks.values();
    }

    @Override
    public Task getTask(int idTask) { // Получение задачи по ее id номеру
        if (tasks.containsKey(idTask)) {
            history.addHistory(tasks.get(idTask));
            return tasks.get(idTask);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpic(int idEpic) {
        if (epics.containsKey(idEpic)) {
            history.addHistory(epics.get(idEpic));
            return epics.get(idEpic);
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubTask(int idSubTask) {
        if (subTasks.containsKey(idSubTask)) {
            history.addHistory(subTasks.get(idSubTask));
            return subTasks.get(idSubTask);
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Integer> getSubTaskForEpic(int idEpic) { // Получение подзадач по id эпика
        if (getEpic(idEpic) != null) {
            return getEpic(idEpic).getSubTasksId();
        } else {
            return null;
        }
    }

    @Override
    public void clearTasks() { // Удаление всех задач
        tasks.clear();
    }

    @Override
    public void clearEpic() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void clearSubTask() {
        subTasks.clear();
        for (Integer id : epics.keySet()) {
            if (getEpic(id) != null) {
                getEpic(id).setStatus(TaskStatus.NEW);
                getEpic(id).setSubTasksId(null);
            }
        }
    }

    @Override
    public void removeTask(int idTask) {
        tasks.remove(idTask);
    }

    @Override
    public void removeEpic(int idEpic) {
        if (getSubTaskForEpic(idEpic) != null) {
            for (int i = 0; i < getSubTaskForEpic(idEpic).size(); i++) {
                subTasks.remove(getSubTaskForEpic(idEpic).get(i));
            }
            epics.remove(idEpic);
        }
    }

    @Override
    public void removeSubTask(int idSubTusk) {
        if (getSubTask(idSubTusk) != null) {
            if (getSubTaskForEpic(getSubTask(idSubTusk).getId()) != null) {
                    getSubTaskForEpic(getSubTask(idSubTusk).getEpicId()).remove(idSubTusk);
                    updateStatusEpic(getSubTask(idSubTusk).getEpicId());
                    subTasks.remove(idSubTusk);
            }
        }
    }

    @Override
    public void updateTask(Task task) { // Обновление всех типов задач
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            updateStatusEpic(subTask.getEpicId());
        }
    }

    @Override
    public void updateStatusEpic(int idEpic) {    //Обновление статуса Epic'а
        int statusInProgress = 0;
        int statusDone = 0;
        if (getSubTaskForEpic(idEpic) != null) {
            for (int i = 0; i < getSubTaskForEpic(idEpic).size(); i++) {
                if (getSubTask(getSubTaskForEpic(idEpic).get(i)) != null) {
                    if (getSubTask(getSubTaskForEpic(idEpic).get(i)).getStatus().equals(TaskStatus.IN_PROGRESS)) {
                        ++statusInProgress;
                    } else {
                        ++statusDone;
                    }
                }
            }
            if (statusInProgress == 0 && statusDone == 0) {
                if (getEpic(idEpic) != null) {
                    getEpic(idEpic).setStatus(TaskStatus.NEW);
                }
            } else if (statusInProgress >= 1) {
                if (getEpic(idEpic) != null) {
                    getEpic(idEpic).setStatus(TaskStatus.IN_PROGRESS);
                }
            } else {
                if (getEpic(idEpic) != null) {
                    getEpic(idEpic).setStatus(TaskStatus.DONE);
                }
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }
}
