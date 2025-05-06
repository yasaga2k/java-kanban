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

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.getFirst());
    }


    @Test
    void shouldNotAddDuplicateTask() {
        Task task = new Task("Повтор", "Описание", Status.NEW);
        task.setId(1);

        historyManager.addToHistory(task);
        historyManager.addToHistory(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task, history.getFirst());
    }


    @Test
    void historyShouldNotExceedLimit() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Задача " + i, "Описание", Status.NEW);
            task.setId(i);
            historyManager.addToHistory(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(9, history.size());
        assertEquals(7, history.get(0).getId());
        assertEquals(15, history.get(8).getId());
    }


    @Test
    void shouldRemoveFromBeginningMiddleEnd() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Задача " + i, "Описание", Status.NEW);
            task.setId(i);
            historyManager.addToHistory(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(5, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
    }
}