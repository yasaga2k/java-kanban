public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Пример 1", "Описание 1", Status.NEW);
        taskManager.createTask(task1);

        Task task2 = new Task("Пример 2", "Описание 2", Status.IN_PROGRESS);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Пример 3", "Описание 3", Status.DONE);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Саб 1", "Описание 1", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Саб 2", "Описание 2", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание 2.2", Status.NEW);
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Саб 3 ", "Описание 3.3", Status.IN_PROGRESS, epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("Subtasks: " + taskManager.getAllSubtask());
        System.out.println("Epic1 subtasks: " + taskManager.getEpicSubtasks(epic1.getId()));

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        System.out.println("Обновление эпиков: " + taskManager.getAllEpics());
        System.out.println("Обновление сабтасков: " + taskManager.getAllSubtask());

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic2.getId());

        System.out.println("Удаление тасков: " + taskManager.getAllTasks());
        System.out.println("Удаление эпиков: " + taskManager.getAllEpics());
        System.out.println("Удаление сабтасков: " + taskManager.getAllSubtask());

    }
}

