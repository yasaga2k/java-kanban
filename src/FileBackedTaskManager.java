import model.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;


    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtask()) {
                writer.write(taskToString(subtask) + "\n");
            }

            writer.write("\n" + historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String taskToString(Task task) {
        String epicId = "";
        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                epicId
        );
    }

    private String historyToString(HistoryManager manager) {
        List<String> ids = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            ids.add(String.valueOf(task.getId()));
        }
        return String.join(",", ids);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] parts = content.split("\n\n");

            // Восстанавливаем задачи
            if (parts.length > 0) {
                String[] lines = parts[0].split("\n");
                for (int i = 1; i < lines.length; i++) { // Пропускаем заголовок
                    Task task = fromString(lines[i]);
                    if (task != null) {
                        if (task instanceof Epic) {
                            manager.addEpic((Epic) task);
                        } else if (task instanceof Subtask) {
                            manager.addSubtask((Subtask) task);
                        } else {
                            manager.addTask(task);
                        }
                    }
                }
            }

            if (parts.length > 1) {
                for (String id : parts[1].split(",")) {
                    int taskId = Integer.parseInt(id);
                    if (manager.tasks.containsKey(taskId)) {
                        manager.historyManager.addToHistory(manager.tasks.get(taskId));
                    } else if (manager.epics.containsKey(taskId)) {
                        manager.historyManager.addToHistory(manager.epics.get(taskId));
                    } else if (manager.subtasks.containsKey(taskId)) {
                        manager.historyManager.addToHistory(manager.subtasks.get(taskId));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);

        // Обработка description
        String description;
        if (fields.length > 4) {
            description = fields[4];
        } else {
            description = "";
        }

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                return epic;
            case SUBTASK:
                // Обработка epicId
                int epicId;
                if (fields.length > 5) {
                    epicId = Integer.parseInt(fields[5]);
                } else {
                    epicId = -1;
                }
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                return null;
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic getEpicsById(int id) {
        Epic epic = super.getEpicsById(id);
        save();
        return epic;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

}