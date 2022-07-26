package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public interface TaskManager {

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    Map<Integer, Task> getAllTask();

    Map<Integer, Epic> getAllEpic();

    Map<Integer, SubTask> getAllSubTask();

    Task getTask(int idTask);

    Epic getEpic(int idEpic);

    SubTask getSubTask(int idSubTask);

    List<Integer> getSubTaskForEpic(int idEpic);

    void clearTasks();

    void clearEpic();

    void clearSubTask();

    void removeTask(int idTask);

    void removeEpic(int idEpic);

    void removeSubTask(int idSubTusk);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    SubTask updateSubTask(SubTask subTask);

    void updateStatusEpic(int idEpic);

    List<? extends Task> getHistory();

    LocalDateTime getterEpicStartTime(List<Integer> listOfSubtaskIdOfTheFirstEpic);

    long getterEpicDuration(List<Integer> listOfSubtaskIdOfTheFirstEpic);

    LocalDateTime getterEpicEndTime(List<Integer> listOfSubTaskId);

    Set<Task> getterPrioritizedTasks();
}
