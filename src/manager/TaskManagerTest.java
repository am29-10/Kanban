package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    void updateTaskManager() {
        taskManager = createTaskManager();
    }

    @Test
    void createTask() {
        Task task = new Task("Task_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTask(1), "Задача не создалась");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpic(1),"Задача не создалась");
    }

    @Test
    void createSubTask() {
        Epic epic = new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60, epic.getId());
        taskManager.createSubTask(subTask);
        assertEquals(taskManager.getSubTask(2), subTask, "Подзадача не создалась");
    }

    @Test
    void getAllTask() {
        taskManager.createTask(new Task("Task_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createTask(new Task("Task_2", "description", TaskStatus.DONE, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createTask(new Task("Task_3", "description", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 3, 3, 3, 3), 60));
        assertEquals(3, taskManager.getAllTask().size(), "Количество созданных задач не соответствует" +
                " ожиданию");
    }

    @Test
    void getAllEpic() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createEpic(new Epic("Epic_2", "description", TaskStatus.DONE, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createEpic(new Epic("Epic_3", "description", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 3, 3, 3, 3), 60));
        assertEquals(3, taskManager.getAllEpic().size(), "Количество созданных задач не соответствует" +
                " ожиданию");
    }

    @Test
    void getAllSubTask() {
        Epic epic = new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        taskManager.createEpic(epic);
        taskManager.createSubTask(new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60, epic.getId()));
        taskManager.createSubTask(new SubTask("SubTask_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60, epic.getId()));
        taskManager.createSubTask(new SubTask("SubTask_3", "description", TaskStatus.NEW, LocalDateTime.of(2022, 4, 4, 4, 4), 60, epic.getId()));
        assertEquals(3, taskManager.getAllSubTask().size(), "Количество созданных подзадач не соответствует" +
                " ожиданию");
    }

    @Test
    void getTaskById() {
        Task task = new Task("Task_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        assertEquals(task, taskManager.getTask(1));
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        taskManager.createEpic(epic);
        taskManager.getEpic(epic.getId());
        assertEquals(epic, taskManager.getEpic(1));
    }

    @Test
    void getSubTaskById() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        SubTask subTask = new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        taskManager.createSubTask(subTask);
        assertEquals(subTask, taskManager.getSubTask(2));
    }

    @Test
    void getSubTaskForEpicById() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createSubTask(new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_3", "description", TaskStatus.NEW, LocalDateTime.of(2022, 4, 4, 4, 4), 60, 1));
        assertEquals(3, taskManager.getSubTaskForEpic(1).size());
    }

    @Test
    void clearTask() {
        taskManager.createTask(new Task("Task_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createTask(new Task("Task_2", "description", TaskStatus.DONE, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createTask(new Task("Task_3", "description", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 3, 3, 3, 3), 60));
        taskManager.clearTasks();
        assertTrue(taskManager.getAllTask().isEmpty());
    }

    @Test
    void clearEpic() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createEpic(new Epic("Epic_2", "description", TaskStatus.DONE, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createEpic(new Epic("Epic_3", "description", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 3, 3, 3, 3), 60));
        taskManager.clearEpic();
        assertTrue(taskManager.getAllEpic().isEmpty());
    }

    @Test
    void clearSubTask() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createSubTask(new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_2", "description", TaskStatus.DONE, LocalDateTime.of(2022, 3, 3, 3, 3), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_3", "description", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 4, 4, 4, 4), 60, 1));
        taskManager.clearSubTask();
        assertTrue(taskManager.getAllSubTask().isEmpty());
    }

    @Test
    void removeTask() {
        taskManager.createTask(new Task("Task_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createTask(new Task("Task_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createTask(new Task("Task_3", "description", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60));
        taskManager.removeTask(1);
        assertEquals(2, taskManager.getAllTask().size());
    }

    @Test
    void removeEpic() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createEpic(new Epic("Epic_2", "description", TaskStatus.DONE, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createEpic(new Epic("Epic_3", "description", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 3, 3, 3, 3), 60));
        taskManager.removeEpic(1);
        assertEquals(2, taskManager.getAllEpic().size());
    }

    @Test
    void removeSubTask() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createSubTask(new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_2", "description", TaskStatus.DONE, LocalDateTime.of(2022, 3, 3, 3, 3), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_3", "description", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 4, 4, 4, 4), 60, 1));
        taskManager.removeSubTask(2);
        taskManager.removeSubTask(3);
        assertEquals(1, taskManager.getAllSubTask().size());
    }

    @Test
    void updateTask() {
        taskManager.createTask(new Task("Task_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createTask(new Task("Task_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createTask(new Task("Task_3", "description", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60));
        Task task = new Task("Task_2_new", "description", TaskStatus.NEW, LocalDateTime.now(), 60);
        task.setId(2);
        taskManager.updateTask(task);
        assertEquals("Task_2_new", taskManager.getTask(2).getTitle());
    }

    @Test
    void updateEpic() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createEpic(new Epic("Epic_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createEpic(new Epic("Epic_3", "description", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60));
        Epic epic = new Epic("Epic_2_new", "description", TaskStatus.NEW, LocalDateTime.of(2022, 4, 4, 4, 4), 60);
        epic.setId(2);
        taskManager.updateEpic(epic);
        assertEquals("Epic_2_new", taskManager.getEpic(2).getTitle());
    }

    @Test
    void updateSubTusk() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.now(), 60));
        taskManager.createSubTask(new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_3", "description", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60, 1));
        SubTask subTask = new SubTask("SubTask_2_new", "description", TaskStatus.NEW, LocalDateTime.of(2022, 4, 4, 4, 4), 60, 1);
        subTask.setId(2);
        taskManager.updateSubTask(subTask);
        assertEquals("SubTask_2_new", taskManager.getSubTask(2).getTitle());
    }

    @Test
    void updateStatusEpic() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createSubTask(new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_3", "description",
                TaskStatus.IN_PROGRESS, LocalDateTime.now(), 60, 1));
        taskManager.updateStatusEpic(1);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(1).getStatus());
    }

    @Test
    void getHistory() {
        taskManager.createEpic(new Epic("Epic_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60));
        taskManager.createEpic(new Epic("Epic_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 2, 2, 2, 2), 60));
        taskManager.createSubTask(new SubTask("SubTask_1", "description", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_2", "description", TaskStatus.NEW, LocalDateTime.of(2022, 4, 4, 4, 4), 60, 1));
        taskManager.createSubTask(new SubTask("SubTask_3", "description",
                TaskStatus.IN_PROGRESS, LocalDateTime.now(), 60, 1));
        taskManager.getAllEpic();
        taskManager.getAllSubTask();
        assertEquals(5, taskManager.getHistory().size());
    }


}

