package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        if (path.equals("/tasks") && query == null) {
            String response = gson.toJson(taskManager.getAllTasks());
            sendSuccess(exchange, response);
        } else if (path.matches("/tasks/\\d+")) {
            try {
                int id = Integer.parseInt(path.substring(7));
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    sendSuccess(exchange, gson.toJson(task));
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Invalid JSON format");
            }
        } else {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            String requestBody = readRequest(exchange);
            Task task = gson.fromJson(requestBody, Task.class);

            if (task == null) {
                sendBadRequest(exchange, "Invalid JSON format");
                return;
            }

            if (taskManager.hasTimeIntersection(task)) {
                sendHasInteractions(exchange);
                return;
            }

            if (task.getId() == 0) {
                taskManager.addTask(task);
                sendCreated(exchange, gson.toJson(task));
            } else {
                taskManager.updateTask(task);
                sendSuccess(exchange, gson.toJson(task));
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Invalid JSON format");
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/tasks")) {
            taskManager.deleteAllTasks();
            sendSuccess(exchange, "{\"message\":\"All tasks deleted\"}");
        } else if (path.matches("/tasks/\\d+")) {
            try {
                int id = Integer.parseInt(path.substring(7));
                taskManager.deleteTaskById(id);
                sendSuccess(exchange, "{\"message\":\"Task deleted\"}");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Invalid JSON format");
            }
        } else {
            sendNotFound(exchange);
        }
    }
}