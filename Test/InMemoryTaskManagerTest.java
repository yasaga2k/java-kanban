import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    void init() {
        manager = new InMemoryTaskManager();
    }


    @Test
    void addAndGetTaskShouldReturnSameTask() {
        Task task = new Task("Проверка", "Описание задачи", Status.NEW);
        manager.addTask(task);
        Task fetched = manager.getTaskById(task.getId());

        assertNotNull(fetched);
        assertEquals(task.getId(), fetched.getId());
        assertEquals(task.getTitle(), fetched.getTitle());
    }


    @Test
    void historyShouldContainRecentlyViewedTasks() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1.getId(), history.get(0).getId());
        assertEquals(task2.getId(), history.get(1).getId());
    }


    @Test
    void addAndGetEpicShouldWorkCorrectly() {
        Epic epic = new Epic("Эпик 1", "Опис 1", Status.NEW);
        manager.addEpic(epic);
        Epic result = manager.getEpicsById(epic.getId());

        assertNotNull(result);
        assertEquals(epic.getId(), result.getId());
        assertEquals(epic.getTitle(), result.getTitle());
    }


    @Test
    void subtaskShouldLinkToEpicCorrectly() {
        Epic epic = new Epic("Эпик 2", "Опис 2",Status.IN_PROGRESS);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Саба", "Опис", Status.NEW,epic.getId());
        manager.addSubtask(subtask);

        Subtask result = manager.getSubtaskIds(subtask.getId());
        assertNotNull(result);
        assertEquals(epic.getId(), result.getEpicId());
    }


    @Test
    void epicStatusShouldBeUpdatedFromSubtasks() {
        Epic epic = new Epic("Эпик 3", "Опис 3", Status.IN_PROGRESS);
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Саб о", "Опис о", Status.NEW,epic.getId());
        Subtask sub2 = new Subtask("Саб е", "Опис е", Status.NEW,epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);

        assertEquals(Status.DONE, manager.getEpicsById(epic.getId()).getStatus());
    }


    @Test
    void removeTaskShouldDeleteTask() {
        Task task = new Task("Таск т", "Опис т", Status.DONE);
        manager.addTask(task);
        int taskId = task.getId();

        manager.deleteTaskById(taskId);
        Task fetched = manager.getTaskById(taskId);

        assertNull(fetched);
    }


    @Test
    void taskStatusShouldBeUpdated() {
        Task task = new Task("Таска изм", "Опис", Status.NEW);
        manager.addTask(task);

        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);

        Task updatedTask = manager.getTaskById(task.getId());
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
    }


    @Test
    void removeEpicShouldDeleteAllSubtasks() {
        Epic epic = new Epic("Праздник", "Организация дня", Status.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Саб", "Опис", Status.NEW,epic.getId());
        manager.addSubtask(subtask);

        manager.deleteEpicById(epic.getId());

        Subtask deletedSubtask = manager.getSubtaskIds(subtask.getId());
        assertNull(deletedSubtask);
    }


    @Test
    void taskShouldHaveUniqueId() {
        Task task1 = new Task("Первая задача", "Описание", Status.NEW);
        Task task2 = new Task("Вторая задача", "Описание", Status.NEW);

        manager.addTask(task1);
        manager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }


    @Test
    void updateEpicShouldNotDuplicateSubtaskIds() {
        Epic epic = new Epic("Эпик", "Опис", Status.NEW);
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Саб 1", "Опис 1", Status.NEW, epic.getId());
        Subtask sub2 = new Subtask("Саб 2", "Опис 2", Status.NEW,epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        Epic updatedEpic = new Epic("Эпика", "Опис", Status.NEW);
        updatedEpic.setId(epic.getId());
        manager.updateEpic(updatedEpic);

        Epic result = manager.getEpicsById(epic.getId());
        List<Integer> subtaskIds = result.getSubtasksIds();

        assertEquals(2, subtaskIds.size());
        assertTrue(subtaskIds.contains(sub1.getId()));
        assertTrue(subtaskIds.contains(sub2.getId()));
    }
}