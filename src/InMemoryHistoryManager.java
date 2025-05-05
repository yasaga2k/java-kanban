import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private final Map<Integer, Task> historyValue = new HashMap<>();

    @Override
    public void addToHistory(Task task) {
        remove(task.getId());
        history.add(task);
        historyValue.put(task.getId(), task);

        if (history.size() >= 10) {
            Task oldest = history.removeFirst();
            historyValue.remove(oldest.getId());
        }
    }

    @Override
    public void remove(int id) {
        Task toRemove = historyValue.remove(id);
        if (toRemove != null) {
            history.remove(toRemove);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}

