package tests;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(new File("Test.csv"));
    }

    @Test
    void loadFromFile() {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("Test.csv"));

        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW, LocalDateTime.of(2022, 1, 1, 1, 1), 60);
        manager.createEpic(epic);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, LocalDateTime.of(2022, 2, 2, 2, 2), 60, 1);
        manager.createSubTask(subTaskForEpic1);
        SubTask subTaskForEpic2 = new SubTask("Сделать геометрию", "Конспект по теореме Пифагора", TaskStatus.NEW, LocalDateTime.of(2022, 3, 3, 3, 3), 60, 1);
        manager.createSubTask(subTaskForEpic2);
        SubTask subTaskForEpic3 = new SubTask("Сделать физику", "Конспект по закону Ома", TaskStatus.NEW, LocalDateTime.of(2022, 4, 4, 4, 4), 60, 1);
        manager.createSubTask(subTaskForEpic3);

        FileBackedTasksManager recoveryManager = manager.loadFromFile(new File("Test.csv"));
        assertEquals(epic, recoveryManager.getEpic(1));

    }
}
