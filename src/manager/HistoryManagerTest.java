package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void updateHistoryManager() {
        historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task_1", "description", TaskStatus.NEW, LocalDateTime.now(), 60);
        task.setId(1);
        Task task2 = new Task("Task_2", "description", TaskStatus.NEW, LocalDateTime.now(), 60);
        task2.setId(2);
        Task task3 = new Task("Task_3", "description", TaskStatus.NEW, LocalDateTime.now(), 60);
        task3.setId(3);
        historyManager.addHistory(task);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);

    }

    @Test
    void addHistory() {
        List<Task> history = historyManager.getHistory();
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    void removeHistory() {
        historyManager.removeHistory(1);
        historyManager.removeHistory(2);
        historyManager.removeHistory(3);
        historyManager.getHistory();
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void getHistory() {
        assertEquals(3, historyManager.getHistory().size());

    }


}