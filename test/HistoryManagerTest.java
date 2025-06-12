import static org.junit.jupiter.api.Assertions.*;

import model.Status;
import model.Task;
import org.junit.jupiter.api.*;

import java.util.List;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Test", "Description", Status.NEW);
        task.setId(1);
        historyManager.addToHistory(task);
        assertEquals(List.of(task), historyManager.getHistory());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task = new Task("Test", "Description", Status.NEW);
        task.setId(1);
        historyManager.addToHistory(task);
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty());
    }
}