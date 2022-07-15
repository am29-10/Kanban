package tasks;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldCreateEpicWithoutSubtasks() {
        Epic epic = new Epic("Test", "Пустой список подзадач", TaskStatus.NEW, LocalDateTime.now(), 60);
        assertTrue(epic.getSubTasksId().isEmpty(), "Список должен быть пустым");
    }

    @Test
    void shouldCreateEpicWithSubtasksStatusNew() {
        Epic epic = new Epic("Test", "Все статусы подзадач равны NEW", TaskStatus.NEW, LocalDateTime.now(), 60);
        SubTask subTask1 = new SubTask("Test", "description", TaskStatus.NEW, LocalDateTime.now(), 60, 1);
        SubTask subTask2 = new SubTask("Test", "description", TaskStatus.NEW, LocalDateTime.now(), 60, 1);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Неверный статус эпика");
        assertEquals(TaskStatus.NEW, subTask1.getStatus(), "Неверный статус подзадачи");
        assertEquals(TaskStatus.NEW, subTask2.getStatus(), "Неверный статус подзадачи");
    }

    @Test
    void shouldCreateEpicWithSubtasksStatusDone() {
        Epic epic = new Epic("Test", "Все статусы подзадач равны DONE", TaskStatus.DONE, LocalDateTime.now(), 60);
        SubTask subTask1 = new SubTask("Test", "description", TaskStatus.DONE, LocalDateTime.now(), 60, 1);
        SubTask subTask2 = new SubTask("Test", "description", TaskStatus.DONE, LocalDateTime.now(), 60, 1);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Неверный статус эпика");
        assertEquals(TaskStatus.DONE, subTask1.getStatus(), "Неверный статус подзадачи");
        assertEquals(TaskStatus.DONE, subTask2.getStatus(), "Неверный статус подзадачи");
    }

    @Test
    void shouldCreateEpicWithoutSubtasksStatusInProgress() {
        Epic epic = new Epic("Test", "Все статусы подзадач равны NEW или DONE", TaskStatus.NEW, LocalDateTime.now(), 60);
        SubTask subTask1 = new SubTask("Test", "description", TaskStatus.NEW, LocalDateTime.now(), 60, 1);
        SubTask subTask2 = new SubTask("Test", "description", TaskStatus.DONE, LocalDateTime.now(), 60, 1);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Неверный статус эпика");
        assertEquals(TaskStatus.NEW, subTask1.getStatus(), "Неверный статус подзадачи");
        assertEquals(TaskStatus.DONE, subTask2.getStatus(), "Неверный статус подзадачи");
    }

    @Test
    void shouldCreateEpicWithSubtasksStatusInProgress() {
        Epic epic = new Epic("Test", "Все статусы подзадач равны IN_PROGRESS", TaskStatus.IN_PROGRESS, LocalDateTime.now(), 60);
        SubTask subTask1 = new SubTask("Test", "description", TaskStatus.IN_PROGRESS, LocalDateTime.now(), 60, 1);
        SubTask subTask2 = new SubTask("Test", "description", TaskStatus.IN_PROGRESS, LocalDateTime.now(), 60, 1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");
        assertEquals(TaskStatus.IN_PROGRESS, subTask1.getStatus(), "Неверный статус подзадачи");
        assertEquals(TaskStatus.IN_PROGRESS, subTask2.getStatus(), "Неверный статус подзадачи");
    }

}