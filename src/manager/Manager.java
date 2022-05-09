package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Manager {
    private int idTask = 0;
    private int idEpic = 0;
    private int idSubTusk = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    public Manager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public void createTask(Task task) { // Создание задачи конкретного типа
        task.setId(++idTask);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(++idEpic);
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
            subTask.setId(++idSubTusk);
            subTasks.put(subTask.getId(), subTask);
        if (getSubTaskForEpic(subTask.getId()) != null) {
            getSubTaskForEpic(subTask.getEpicId()).add(subTask.getId());
            updateStatusEpic(subTask.getEpicId());
        }

    }

    public Collection<Task> getAllTask() { // Получение массива всех задач для конкретного типа задачи
        return tasks.values();
    }

    public Collection<Epic> getAllEpic() {
        return epics.values();
    }

    public Collection<SubTask> getAllSubTask() {
        return subTasks.values();
    }

    public Task getTask(int idTask) { // Получение задачи по ее id номеру
        if (tasks.containsKey(idTask)) {
            return tasks.get(idTask);
        } else {
            return null;
        }
    }

    public Epic getEpic(int idEpic) {
        if (epics.containsKey(idEpic)) {
            return epics.get(idEpic);
        } else {
            return null;
        }
    }

    public SubTask getSubTask(int idSubTask) {
        if (subTasks.containsKey(idSubTask)) {
            return subTasks.get(idSubTask);
        } else {
            return null;
        }
    }

    public ArrayList<Integer> getSubTaskForEpic(int idEpic) { // Получение подзадач по id эпика
        if (getEpic(idEpic) != null) {
            return getEpic(idEpic).getSubTasksId();
        } else {
            return null;
        }
    }

    public void clearTasks() { // Удаление всех задач
        tasks.clear();
    }

    public void clearEpic() {
        epics.clear();
        subTasks.clear();
    }

    public void clearSubTask() {
        subTasks.clear();
        for (Integer id : epics.keySet()) {
            getEpic(id).setStatus(TaskStatus.NEW);
            getEpic(id).setSubTasksId(null);
        }
    }

    public void removeTask(int idTask) {
        if (tasks.containsKey(idTask)) {
            tasks.remove(idTask);
        }
    }

    public void removeEpic(int idEpic) {
        if ((epics.containsKey(idEpic)) && (getSubTaskForEpic(idEpic) != null)) {
            for (int i = 0; i < getSubTaskForEpic(idEpic).size(); i++) {
                subTasks.remove(getSubTaskForEpic(idEpic).get(i));
            }
            epics.remove(idEpic);
        }
    }

    public void removeSubTask(int idSubTusk) {
        if ((subTasks.containsKey(idSubTusk)) && (getSubTaskForEpic(getSubTask(idSubTusk).getId()) != null)) {
            getSubTaskForEpic(getSubTask(idSubTusk).getEpicId()).remove(idSubTusk);
            updateStatusEpic(getSubTask(idSubTusk).getEpicId());
            subTasks.remove(idSubTusk);
        }
    }

    public void updateTask(Task task) { // Обновление всех типов задач
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            updateStatusEpic(subTask.getEpicId());
        }
    }


    public void updateStatusEpic(int idEpic) {    //Обновление статуса Epic'а
        int statusInProgress = 0;
        int statusDone = 0;
        if (getSubTaskForEpic(idEpic) != null) {
            for (int i = 0; i < getSubTaskForEpic(idEpic).size(); i++) {
                if (getSubTask(getSubTaskForEpic(idEpic).get(i)).getStatus().equals(TaskStatus.IN_PROGRESS)) {
                    ++statusInProgress;
                } else {
                    ++statusDone;
                }
            }
            if (statusInProgress < 1 && statusDone < 1) {
                getEpic(idEpic).setStatus(TaskStatus.NEW);
            } else if (statusInProgress >= 1) {
                getEpic(idEpic).setStatus(TaskStatus.IN_PROGRESS);
            } else {
                getEpic(idEpic).setStatus(TaskStatus.DONE);
            }
        }
    }
}
