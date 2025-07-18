import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.LocalDateTime;
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

        Subtask result = manager.getSubtaskById(subtask.getId());
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

        Subtask deletedSubtask = manager.getSubtaskById(subtask.getId());
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
        Subtask sub2 = new Subtask("Саб 2", "Опис 2", Status.NEW, epic.getId());
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

    @Test
    void historyShouldNotContainDuplicates() {
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.addTask(task);

        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void deletedTaskShouldBeRemovedFromHistory() {
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.addTask(task);
        manager.getTaskById(task.getId());

        manager.deleteTaskById(task.getId());
        assertFalse(manager.getHistory().contains(task));
    }

    @Test
    void deletedEpicShouldRemoveSubtasksFromHistory() {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        manager.getEpicsById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        manager.deleteEpicById(epic.getId());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void historyShouldNotExceedMaxSize() {
        for (int i = 1; i <= 20; i++) {
            Task task = new Task("Task" + i, "Desc", Status.NEW);
            manager.addTask(task);
            manager.getTaskById(task.getId());
        }

        assertTrue(manager.getHistory().size() <= 20);
    }

    @Test
    void historyShouldPreserveOrderAfterRemoval() {
        Task task1 = new Task("Task1", "Desc1", Status.NEW);
        Task task2 = new Task("Task2", "Desc2", Status.NEW);
        Task task3 = new Task("Task3", "Desc3", Status.NEW);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());

        manager.deleteTaskById(task2.getId());

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1.getId(), history.get(0).getId());
        assertEquals(task3.getId(), history.get(1).getId());
    }

    @Test
    void shouldCalculateEpicTimeBasedOnItsSubtasks() {
        Epic epic = new Epic("Эпик", "Тестовое время", Status.NEW);
        manager.createEpic(epic);  // используем create

        Subtask s1 = new Subtask("Подзадача 1", "Первая", Status.NEW, epic.getId());
        s1.setStartTime(LocalDateTime.of(2025, 6, 5, 9, 0));
        s1.setDuration(Duration.ofMinutes(60));
        manager.createSubtask(s1);

        Subtask s2 = new Subtask("Подзадача 2", "Вторая", Status.NEW, epic.getId());
        s2.setStartTime(LocalDateTime.of(2025, 6, 5, 11, 0));
        s2.setDuration(Duration.ofMinutes(30));
        manager.createSubtask(s2);

        Epic resultEpic = manager.getEpicsById(epic.getId());

        assertEquals(LocalDateTime.of(2025, 6, 5, 9, 0), resultEpic.getStartTime());
        assertEquals(Duration.ofMinutes(90), resultEpic.getDuration());
        assertEquals(LocalDateTime.of(2025, 6, 5, 11, 30), resultEpic.getEndTime());
    }

    @Test
    void prioritizedTasksShouldBeOrderedByStartTimeOnly() {
        Task task1 = new Task("Таска1", "Описание", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 6, 5, 8, 0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task("Таска2", "Описание", Status.NEW); // без startTime — не попадет
        manager.createTask(task2);

        Epic epic = new Epic("Эпик", "Для подзадачи", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание", Status.NEW, epic.getId());
        subtask1.setStartTime(LocalDateTime.of(2025, 6, 5, 7, 0));
        subtask1.setDuration(Duration.ofMinutes(15));
        manager.createSubtask(subtask1);

        List<Task> prioritized = manager.getPrioritizedTasks();

        assertEquals(2, prioritized.size()); // только с startTime
        assertEquals(subtask1.getId(), prioritized.get(0).getId());
        assertEquals(task1.getId(), prioritized.get(1).getId());
    }

    @Test
    void shouldThrowWhenAddingOverlappingTask() {
        Task task1 = new Task("Таска1", "Описание1", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 6, 5, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.addTask(task1); // этот метод использует проверку на пересечение

        Task overlappingTask = new Task("Таска2", "Описание2", Status.NEW);
        overlappingTask.setStartTime(LocalDateTime.of(2025, 6, 5, 10, 30)); // пересекается
        overlappingTask.setDuration(Duration.ofMinutes(30));

        assertThrows(IllegalArgumentException.class, () -> manager.addTask(overlappingTask));
    }

    @Test
    void shouldThrowWhenUpdatingTaskWithTimeConflict() {
        Task task1 = new Task("Таска1", "Описание1", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 6, 5, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.createTask(task1);

        Task task2 = new Task("Таска2", "Описание2", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 6, 5, 12, 0));
        task2.setDuration(Duration.ofMinutes(60));
        manager.createTask(task2);

        task2.setStartTime(LocalDateTime.of(2025, 6, 5, 10, 30)); // конфликт

        assertThrows(IllegalArgumentException.class, () -> manager.updateTask(task2));
    }
}