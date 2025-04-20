import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TaskManager {
    private int numberId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int newId() {
        return ++numberId;
    }

    // Методы ТАСКА
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        task.setId(newId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // МЕТОДЫ ЭПИКА
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicsById(int id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        epic.setId(newId());
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);

        if (epic != null) {
            List<Integer> subtasksId = epic.getSubtasksIds();
            for (int subtaskId : subtasksId) {
                deleteSubtaskById(subtaskId);
            }
        }

    }

    // МЕТОДЫ САБТАСКА
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }


    public void deleteAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic);
        }
    }


    public Subtask getSubtaskIds(int id) {
        return subtasks.get(id);
    }


    public void createSubtask(Subtask subtask) {
        subtask.setId(newId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtasksId(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpicStatus(epic);
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
        }
    }

    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        List<Subtask> subtasksForEpic = new ArrayList<>();
        for (int subtaskId : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                subtasksForEpic.add(subtask);
            }
        }
        return subtasksForEpic;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(Task.Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask.getStatus() != Task.Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Task.Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Task.Status.DONE);
        } else if (allNew) {
            epic.setStatus(Task.Status.NEW);
        } else {
            epic.setStatus(Task.Status.IN_PROGRESS);
        }

    }

}
