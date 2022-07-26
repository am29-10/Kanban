package manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HttpTaskManager getDefaultManager(String url, String keyForSave) {
        return new HttpTaskManager(url, keyForSave);
    }

    public static FileBackedTasksManager getDefaultBackedManager() {
        return new FileBackedTasksManager(new File("Test.csv"));
    }
}
