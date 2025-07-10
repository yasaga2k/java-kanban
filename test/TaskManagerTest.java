import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;


class TaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void initialization() {
        manager = new InMemoryTaskManager();
    }


    @Test
    void addAndGetTaskInterface() {
        Task task = new Task("Проверка", "Описание", Status.NEW);
        manager.createTask(task);
        Task result = manager.getTaskById(task.getId());

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
    }


    @Test
    void addAndGetEpicInterface() {
        Epic epic = new Epic("Эпик", "Описание эпика", Status.NEW);
        manager.createEpic(epic);
        Epic result = manager.getEpicsById(epic.getId());

        assertNotNull(result);
        assertEquals(epic.getId(), result.getId());
    }


    @Test
    void addAndGetSubtaskInterface() {
        Epic epic = new Epic("Эпик", "Описание эпика", Status.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        Subtask result = manager.getSubtaskById(subtask.getId());
        assertNotNull(result);
        assertEquals(epic.getId(), result.getEpicId());
    }
}