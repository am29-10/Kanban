package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;
    protected final HistoryManager history;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.history = Managers.getDefaultHistory();
    }

    @Override
    public void createTask(Task task) { // Создание задачи конкретного типа
        task.setId(++id);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
            subTask.setId(++id);
            subTasks.put(subTask.getId(), subTask);
        if (epics.containsKey(subTask.getEpicId())) {
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
    public Task getTask(int id) { // Получение задачи по ее id номеру
        if (tasks.containsKey(id)) {
            history.addHistory(tasks.get(id));
            return tasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            history.addHistory(epics.get(id));
            return epics.get(id);
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
    public List<Integer> getSubTaskForEpic(int id) { // Получение списка id подзадач по id эпика
        if (getEpic(id) != null) {
            return getEpic(id).getSubTasksId();
        } else {
            return null;
        }
    }

    @Override
    public void clearTasks() { // Удаление всех задач
        for (Integer idTask : tasks.keySet()) {
            history.removeHistory(idTask);
        }
        tasks.clear();
    }

    @Override
    public void clearEpic() {
        for (Integer idEpic : epics.keySet()) {
            history.removeHistory(idEpic);
        }
        for (Integer idSubTask : subTasks.keySet()) {
            history.removeHistory(idSubTask);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void clearSubTask() {
        for (Integer idSubTask : subTasks.keySet()) {
            history.removeHistory(idSubTask);
        }
        subTasks.clear();
        for (Integer id : epics.keySet()) {
            if (getEpic(id) != null) {
                getEpic(id).setStatus(TaskStatus.NEW);
                getEpic(id).setSubTasksId(null);
            }
        }
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        history.removeHistory(id);
    }

    @Override
    public void removeEpic(int id) {
        if (getSubTaskForEpic(id) != null) {
            for (Integer idSubTusk : getEpic(id).getSubTasksId()) {
                subTasks.remove(idSubTusk);
                history.removeHistory(idSubTusk);
            }
            epics.remove(id);
            history.removeHistory(id);
        }
    }

    @Override
    public void removeSubTask(int id) {
        if ((getSubTask(id) != null) ) {
            if (getSubTaskForEpic(getSubTask(id).getId()) != null) {
                getSubTaskForEpic(getSubTask(id).getEpicId()).remove(id);
                updateStatusEpic(getSubTask(id).getEpicId());
                subTasks.remove(id);
                history.removeHistory(id);
            } else {
                subTasks.remove(id);
                history.removeHistory(id);
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
    public void updateStatusEpic(int id) {    //Обновление статуса Epic'а
        int statusInProgress = 0;
        int statusDone = 0;
        if (getSubTaskForEpic(id) != null) {
            for (int i = 0; i < getSubTaskForEpic(id).size(); i++) {
                if (getSubTask(getSubTaskForEpic(id).get(i)) != null) {
                    if (getSubTask(getSubTaskForEpic(id).get(i)).getStatus().equals(TaskStatus.IN_PROGRESS)) {
                        ++statusInProgress;
                    } else {
                        ++statusDone;
                    }
                }
            }
            if (statusInProgress == 0 && statusDone == 0) {
                if (getEpic(id) != null) {
                    getEpic(id).setStatus(TaskStatus.NEW);
                }
            } else if (statusInProgress >= 1) {
                if (getEpic(id) != null) {
                    getEpic(id).setStatus(TaskStatus.IN_PROGRESS);
                }
            } else {
                if (getEpic(id) != null) {
                    getEpic(id).setStatus(TaskStatus.DONE);
                }
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

}
