package manager;

import exception.ManagerDateTimeException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;
    protected final HistoryManager history;
    protected final DateTimeFormatter dateTimeFormatter;
    private final Set<Task> listOfPrioritizedTasks = new TreeSet<>((task1, task2) -> {
        if ((task1.getStartTime() != null) && (task2.getStartTime() != null)) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if (task1.getStartTime() == null) {
            return 1;
        } else if (null == task2.getStartTime()) {
            return -1;
        }else {
            return 0;
        }
    });

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        history = Managers.getDefaultHistory();
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    }

    @Override
    public void createTask(Task task) { // Создание задачи конкретного типа
        checkIntersectionByTaskTime(task);
        task.setId(++id);
        tasks.put(task.getId(), task);
        listOfPrioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        checkIntersectionByTaskTime(epic);
        checkIntersectionByTaskTime(epic);
        epic.setId(++id);
        epics.put(epic.getId(), epic);
        listOfPrioritizedTasks.add(epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        checkIntersectionByTaskTime(subTask);
        subTask.setId(++id);
        subTasks.put(subTask.getId(), subTask);
        listOfPrioritizedTasks.add(subTask);
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
        history.removeHistory(id);
        Task task = tasks.get(id);
        if (task != null) {
            listOfPrioritizedTasks.remove(task);
        }
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer idSubTusk : getEpic(id).getSubTasksId()) {
                history.removeHistory(idSubTusk);
                SubTask subTask = subTasks.get(idSubTusk);
                if (subTask != null) {
                    listOfPrioritizedTasks.remove(subTask);
                }
                subTasks.remove(idSubTusk);
            }
            epics.remove(id);
            history.removeHistory(id);
            listOfPrioritizedTasks.remove(epic);
        }
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if ((subTask != null) ) {
            if (getSubTaskForEpic(getSubTask(id).getId()) != null) {
                getSubTaskForEpic(getSubTask(id).getEpicId()).remove(id);
                updateStatusEpic(getSubTask(id).getEpicId());
                subTasks.remove(id);
                history.removeHistory(id);
                listOfPrioritizedTasks.remove(subTask);
            } else {
                subTasks.remove(id);
                history.removeHistory(id);
                listOfPrioritizedTasks.remove(subTask);
            }
        }
    }

    @Override
    public void updateTask(Task task) { // Обновление всех типов задач
        if (tasks.containsKey(task.getId())) {
            checkIntersectionByTaskTime(task);
            tasks.put(task.getId(), task);
            listOfPrioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            checkIntersectionByTaskTime(epic);
            epics.put(epic.getId(), epic);
            listOfPrioritizedTasks.add(epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            checkIntersectionByTaskTime(subTask);
            subTasks.put(subTask.getId(), subTask);
            updateStatusEpic(subTask.getEpicId());
            listOfPrioritizedTasks.add(subTask);
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

    @Override
    public LocalDateTime getterEpicTaskStartTime(List<Integer> listOfSubTaskId) {
        return getEpicTaskStartTime(listOfSubTaskId);
    }

    @Override
    public long getterEpicTaskDuration(List<Integer> listOfSubTaskId) {
        return getEpicTaskDuration(listOfSubTaskId);
    }

    @Override
    public LocalDateTime getterEpicTaskEndTime(List<Integer> listOfSubTaskId) {
        return getEpicTaskEndTime(listOfSubTaskId);
    }

    @Override
    public Set<Task> getterPrioritizedTasks() {
        return listOfPrioritizedTasks;
    }

    public LocalDateTime getEpicTaskStartTime(List<Integer> listOfSubTaskId) {
        try {
            SubTask subTaskForStartTimeMin;

            LocalDateTime startTimeMin = null;
            if (listOfSubTaskId.size() != 0) {
                subTaskForStartTimeMin = subTasks.get(listOfSubTaskId.get(0));
                startTimeMin = subTaskForStartTimeMin.getStartTime();
            }
            for (int i = 1; i < listOfSubTaskId.size(); i++) {
                SubTask subTask = subTasks.get(listOfSubTaskId.get(i));
                if (subTask.getStartTime().isBefore(startTimeMin)) {
                    startTimeMin = subTask.getStartTime();
                }
            }
            return startTimeMin;
        } catch (ClassCastException | NullPointerException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public long getEpicTaskDuration(List<Integer> listOfSubTaskId) {
        long durationEpicTask = 0;
        for (var id : listOfSubTaskId) {
            SubTask subTask = subTasks.get(id);
            if (subTask != null) {
                durationEpicTask += subTask.getDuration();
            }
        }
        return durationEpicTask;
    }

    public LocalDateTime getEpicTaskEndTime(List<Integer> listOfSubTaskId) {
        try {
            SubTask subTaskForStartTimeMax;

            LocalDateTime startTimeMax = null;
            if (listOfSubTaskId.size() != 0) {
                subTaskForStartTimeMax = subTasks.get(listOfSubTaskId.get(0));
                startTimeMax = subTaskForStartTimeMax.getEndTime();
            }
            for (int i = 1; i < listOfSubTaskId.size(); i++) {
                SubTask subTask = subTasks.get(listOfSubTaskId.get(i));
                if (subTask.getEndTime().isAfter(startTimeMax)) {
                    startTimeMax = subTask.getEndTime();
                }
            }
            return startTimeMax;
        } catch (ClassCastException | NullPointerException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void checkIntersectionByTaskTime(Task task) {
        LocalDateTime startTimeTask = task.getStartTime();
        LocalDateTime endTimeTask = task.getEndTime();
        Set<Task> listOfSortedTasks = getPrioritizedTasks();

        for (var taskFromTheList : listOfSortedTasks) {
            if (taskFromTheList.getStartTime() != null) {
                LocalDateTime startTimeTaskFromList = taskFromTheList.getStartTime();
                LocalDateTime endTimeTaskFromList = taskFromTheList.getEndTime();
                if ((startTimeTask.isAfter(startTimeTaskFromList) && startTimeTask.isBefore(endTimeTaskFromList))
                        || (endTimeTask.isAfter(startTimeTaskFromList) && endTimeTask.isBefore(endTimeTaskFromList))
                        || startTimeTaskFromList.isAfter(startTimeTask) && endTimeTaskFromList.isBefore(endTimeTask)
                        || startTimeTask.isAfter(startTimeTaskFromList) && endTimeTask.isBefore(endTimeTaskFromList)) {
                    throw new ManagerDateTimeException("Задачи и подзадачи пересекаются по времени выполнения");
                }
            }
        }
    }

    private Set<Task> getPrioritizedTasks_list() {
        return listOfPrioritizedTasks;
    }

    public Set<Task> getPrioritizedTasks() {
        return getPrioritizedTasks_list();
    }




}
