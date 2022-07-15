package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(new File("Test.csv"));
    }

}
