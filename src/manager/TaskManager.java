package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    List<Task> getAllTask();

    List<Epic> getAllEpic();

    List<SubTask> getAllSubTask();

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

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void updateStatusEpic(int idEpic);

    List<? extends Task> getHistory();

    LocalDateTime getterEpicTaskStartTime(List<Integer> listOfSubtaskIdOfTheFirstEpicTask);

    long getterEpicTaskDuration(List<Integer> listOfSubtaskIdOfTheFirstEpicTask);

    LocalDateTime getterEpicTaskEndTime(List<Integer> listOfSubTaskId);

    Set<Task> getterPrioritizedTasks();
}
