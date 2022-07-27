package manager;

import java.io.File;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HttpTaskManager getDefaultManager(String url, String keyForSave) {
        return new HttpTaskManager(url, keyForSave);
    }
}
