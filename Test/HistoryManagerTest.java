import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.util.List;



class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldCreateTask() {
        Task task = new Task("Test", "Description", Status.NEW);
        task.setId(1);
        historyManager.addToHistory(task);
        assertEquals(List.of(task), historyManager.getHistory());
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = new Task("Test", "Description", Status.NEW);
        task.setId(1);
        historyManager.addToHistory(task);
    }
}