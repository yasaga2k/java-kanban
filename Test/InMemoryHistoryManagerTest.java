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
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }


    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Таск", "Опис", Status.NEW);
        task.setId(1);
        historyManager.addToHistory(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
    }


    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Таск 1", "Опис 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Опис 2", Status.NEW);
        task1.setId(1);
        task2.setId(2);

        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
    }


    @Test
    void shouldNotAddDuplicateTask() {
        Task task = new Task("Повтор", "Описание", Status.NEW);
        task.setId(1);

        historyManager.addToHistory(task);
        historyManager.addToHistory(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
    }


    @Test
    void historyShouldNotExceedLimit() {
        for (int i = 1; i <= 10; i++) {
            Task task = new Task("Задача " + i, "Описание", Status.NEW);
            task.setId(i);
            historyManager.addToHistory(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertEquals(6, history.get(0).getId());
        assertEquals(15, history.get(9).getId());
    }


    @Test
    void shouldRemoveFromBeginningMiddleEnd() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Задача " + i, "Описание", Status.NEW);
            task.setId(i);
            historyManager.addToHistory(task);
        }

        historyManager.remove(1);
        historyManager.remove(3);
        historyManager.remove(5);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(2, history.get(0).getId());
        assertEquals(4, history.get(1).getId());
    }
}