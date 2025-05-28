import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void initialization() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void historyShouldBeEmptyInitially() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setId(1);
        historyManager.addToHistory(task);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveDuplicatesWhenAddingSameTask() {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setId(1);

        historyManager.addToHistory(task);
        historyManager.addToHistory(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldMaintainInsertionOrder() {
        Task task1 = new Task("Task1", "Desc1", Status.NEW);
        Task task2 = new Task("Task2", "Desc2", Status.NEW);
        task1.setId(1);
        task2.setId(2);

        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
    }

    @Test
    void shouldRemoveTaskFromAnyPosition() {
        Task task1 = new Task("Task1", "Desc1", Status.NEW);
        Task task2 = new Task("Task2", "Desc2", Status.NEW);
        Task task3 = new Task("Task3", "Desc3", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        // Удаляем из середины
        historyManager.remove(2);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
    }
}