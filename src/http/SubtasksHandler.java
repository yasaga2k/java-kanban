package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/subtasks")) {
            String response = gson.toJson(taskManager.getAllSubtask());
            sendSuccess(exchange, response);
        } else if (path.matches("/subtasks/\\d+")) {
            try {
                int id = Integer.parseInt(path.substring(10));
                Subtask subtask = taskManager.getSubtaskById(id);
                if (subtask != null) {
                    sendSuccess(exchange, gson.toJson(subtask));
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
            Subtask subtask = gson.fromJson(requestBody, Subtask.class);

            if (subtask == null) {
                sendBadRequest(exchange, "Invalid JSON format");
                return;
            }

            if (taskManager.hasTimeIntersection(subtask)) {
                sendHasInteractions(exchange);
                return;
            }

            if (subtask.getId() == 0) {
                taskManager.addSubtask(subtask);
                sendCreated(exchange, gson.toJson(subtask));
            } else {
                taskManager.updateSubtask(subtask);
                sendSuccess(exchange, gson.toJson(subtask));
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Invalid JSON format");
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/subtasks")) {
            taskManager.deleteAllSubtask();
            sendSuccess(exchange, "{\"message\":\"All subtasks deleted\"}");
        } else if (path.matches("/subtasks/\\d+")) {
            try {
                int id = Integer.parseInt(path.substring(10));
                taskManager.deleteSubtaskById(id);
                sendSuccess(exchange, "{\"message\":\"Subtask deleted\"}");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Invalid JSON format");
            }
        } else {
            sendNotFound(exchange);
        }
    }
}