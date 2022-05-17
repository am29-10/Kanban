package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    Collection<Task> getAllTask();

    Collection<Epic> getAllEpic();

    Collection<SubTask> getAllSubTask();

    Task getTask(int idTask);

    Epic getEpic(int idEpic);

    SubTask getSubTask(int idSubTask);

    ArrayList<Integer> getSubTaskForEpic(int idEpic);

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

}
