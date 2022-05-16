package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history;
    private static final int SIZE_HISTORY = 10;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public void addHistory(Task task) {
        if (task != null) {
            if (history.size() == SIZE_HISTORY) {
                history.remove(0);
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
