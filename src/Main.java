import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        InMemoryTaskManager mainManager = new InMemoryTaskManager();

        Task task1 = new Task("Пример 1", "Описание 1", Status.NEW);
        mainManager.createTask(task1);

        Task task2 = new Task("Пример 2", "Описание 2", Status.IN_PROGRESS);
        mainManager.createTask(task2);

        Epic epic1 = new Epic("Пример 3", "Описание 3", Status.DONE);
        mainManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Саб 1", "Описание 1", Status.NEW, epic1.getId());
        mainManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Саб 2", "Описание 2", Status.NEW, epic1.getId());
        mainManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание 2.2", Status.NEW);
        mainManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Саб 3 ", "Описание 3.3", Status.IN_PROGRESS, epic2.getId());
        mainManager.createSubtask(subtask3);

        System.out.println("Tasks: " + mainManager.getAllTasks());
        System.out.println("Epics: " + mainManager.getAllEpics());
        System.out.println("Subtasks: " + mainManager.getAllSubtask());
        System.out.println("Epic1 subtasks: " + mainManager.getEpicSubtasks(epic1.getId()));

        subtask1.setStatus(Status.DONE);
        mainManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        mainManager.updateSubtask(subtask2);
        System.out.println("Обновление эпиков: " + mainManager.getAllEpics());
        System.out.println("Обновление сабтасков: " + mainManager.getAllSubtask());

        mainManager.deleteTaskById(task1.getId());
        mainManager.deleteEpicById(epic2.getId());

        System.out.println("Удаление тасков: " + mainManager.getAllTasks());
        System.out.println("Удаление эпиков: " + mainManager.getAllEpics());
        System.out.println("Удаление сабтасков: " + mainManager.getAllSubtask());

        mainManager.getTaskById(task1.getId());
        mainManager.getEpicsById(epic1.getId());
        mainManager.getSubtaskById(subtask1.getId());

        System.out.println("\nИстория просмотров:");
        for (Task viewed : mainManager.getHistory()) {
            System.out.println(viewed.getTitle() + viewed.getId());
        }

    }
}

