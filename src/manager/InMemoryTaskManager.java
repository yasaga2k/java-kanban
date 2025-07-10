package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager { // Реализуем интерфейс TaskManager
    private int numberId = 0;
    protected HistoryManager historyManager = new InMemoryHistoryManager();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime)
                    .thenComparing(Task::getId)
    );

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean hasTimeIntersection(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        return prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .anyMatch(existing -> {
                    LocalDateTime existStart = existing.getStartTime();
                    LocalDateTime existEnd = existing.getEndTime();
                    return newStart.isBefore(existEnd) && newEnd.isAfter(existStart);
                });
    }

    private int newId() {
        return ++numberId;
    }

    protected void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    // Методы ТАСКА
    @Override
    public void addTask(Task task) {
        task.setId(newId());
        if (hasTimeIntersection(task)) {
            throw new IllegalArgumentException("Задача пересекается с существующей задачей.");
        }
        tasks.put(task.getId(), task);
        addToPrioritized(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            removeFromPrioritized(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.addToHistory(task);
        }
        return task;
    }

    @Override
    public void createTask(Task task) {
        task.setId(newId());
        if (hasTimeIntersection(task)) {
            throw new IllegalArgumentException("Задача пересекается с другой задачей");
        }
        tasks.put(task.getId(), task);
        addToPrioritized(task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            removeFromPrioritized(tasks.get(task.getId()));
            if (hasTimeIntersection(task)) {
                addToPrioritized(tasks.get(task.getId()));
                throw new IllegalArgumentException("Задача пересекается с другой задачей.");
            }
            tasks.put(task.getId(), task);
            addToPrioritized(task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            removeFromPrioritized(task);
            historyManager.remove(id);
        }
    }

    // МЕТОДЫ ЭПИКА
    @Override
    public void addEpic(Epic epic) {
        epic.setId(newId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicsById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addToHistory(epic);
        }
        return epic;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(newId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        if (oldEpic != null) {
            epic.setSubtasksIds(new ArrayList<>(oldEpic.getSubtasksIds()));
        } else {
            epic.setSubtasksIds(new ArrayList<>());
        }
        epics.put(epic.getId(), epic);
        updateEpicFields(epic.getId());

    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtasksIds()) {
                Subtask subtask = subtasks.remove(subId);
                if (subtask != null) {
                    removeFromPrioritized(subtask);
                    historyManager.remove(subId);
                }
            }
            historyManager.remove(id);
        }
    }

    // МЕТОДЫ САБТАСКА
    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(newId()); // Сначала задаём ID
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic не найден для подзадачи.");
        }
        if (hasTimeIntersection(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается с существующей задачей.");
        }
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtasksId(subtask.getId());
        addToPrioritized(subtask);
        updateEpicFields(epic.getId());
    }

    protected void updateEpicFields(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> subs = subtasks.values().stream()
                .filter(s -> s.getEpicId() == epicId)
                .collect(Collectors.toList());

        epic.updateTimeFields(subs);
        updateEpicStatus(epic, subs);
    }


    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            removeFromPrioritized(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            updateEpicFields(epic.getId());
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.addToHistory(subtask);
        }
        return subtask;
    }


    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic не найден для подзадачи.");
        }
        subtask.setId(newId());
        if (hasTimeIntersection(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается с существующей задачей.");
        }
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtasksId(subtask.getId());
        addToPrioritized(subtask);
        updateEpicFields(epic.getId());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            removeFromPrioritized(subtasks.get(subtask.getId()));
            if (hasTimeIntersection(subtask)) {
                addToPrioritized(subtasks.get(subtask.getId()));
                throw new IllegalArgumentException("Подзадача пересекается с другой задачей.");
            }
            subtasks.put(subtask.getId(), subtask);
            addToPrioritized(subtask);
            updateEpicFields(subtask.getEpicId());
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeFromPrioritized(subtask);
            historyManager.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicFields(epic.getId());
            }
        }
    }


    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        return subtasks.values().stream()
                .filter(s -> s.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    // МЕТОД ИСТОРИИ

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic, List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allDone = subtasks.stream().allMatch(s -> s.getStatus() == Status.DONE);
        boolean allNew = subtasks.stream().allMatch(s -> s.getStatus() == Status.NEW);

        if (allDone) epic.setStatus(Status.DONE);
        else if (allNew) epic.setStatus(Status.NEW);
        else epic.setStatus(Status.IN_PROGRESS);
    }

    // метод для обновления статуса эпика (с использованием getEpicSubtasks)
    private void updateEpicStatus(Epic epic) {
        List<Subtask> subs = getEpicSubtasks(epic.getId());
        updateEpicStatus(epic, subs);
    }

}
