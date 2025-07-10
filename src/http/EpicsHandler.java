package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        try {
            if (path.equals("/epics")) {
                handleGetAllEpics(exchange);
            } else if (path.startsWith("/epics/")) {
                String[] parts = path.split("/");
                if (parts.length == 3 && parts[2].matches("\\d+")) {
                    handleGetEpicById(exchange, Integer.parseInt(parts[2]));
                } else if (parts.length == 4 && parts[2].matches("\\d+") && parts[3].equals("subtasks")) {
                    handleGetEpicSubtasks(exchange, Integer.parseInt(parts[2]));
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllEpics());
        sendSuccess(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicsById(id);

        if (epic != null) {
            sendSuccess(exchange, gson.toJson(epic));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int epicId) throws IOException {
        String response = gson.toJson(taskManager.getEpicSubtasks(epicId));
        sendSuccess(exchange, response);
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            String requestBody = readRequest(exchange);

            Epic epic = gson.fromJson(requestBody, Epic.class);
            if (epic == null) {
                sendBadRequest(exchange, "Invalid epic data");
                return;
            }

            if (epic.getId() == 0) {
                taskManager.addEpic(epic);
                sendCreated(exchange, gson.toJson(epic));
            } else {
                taskManager.updateEpic(epic);
                sendSuccess(exchange, gson.toJson(epic));
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Invalid JSON format");
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        try {
            if (path.equals("/epics")) {
                taskManager.deleteAllEpics();
                sendSuccess(exchange, "All epics deleted");
            } else if (path.startsWith("/epics/")) {
                String[] parts = path.split("/");
                if (parts.length == 3 && parts[2].matches("\\d+")) {
                    int id = Integer.parseInt(parts[2]);
                    taskManager.deleteEpicById(id);
                    sendSuccess(exchange, "Epic with ID " + id + " deleted");
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}