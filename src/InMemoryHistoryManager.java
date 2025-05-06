import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();


    @Override
    public void addToHistory(Task task) {
        history.add(task);
        if (history.size() >= 10) {
            Task oldest = history.removeFirst();

        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}

