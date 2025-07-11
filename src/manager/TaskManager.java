package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    // Методы ТАСКА
    void addTask(Task task);

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

    // МЕТОДЫ ЭПИКА
    void addEpic(Epic epic);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicsById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    // МЕТОДЫ САБТАСКА
    void addSubtask(Subtask subtask);

    List<Subtask> getAllSubtask();

    void deleteAllSubtask();

    Subtask getSubtaskById(int id);

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    List<Subtask> getEpicSubtasks(int epicId);

    // ИСТОРИЯ
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean hasTimeIntersection(Task task);
}
