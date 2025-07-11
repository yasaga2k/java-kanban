package http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/history")) {
            String response = gson.toJson(taskManager.getHistory());
            sendSuccess(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }
}