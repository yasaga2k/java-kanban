package http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/prioritized")) {
            String response = gson.toJson(taskManager.getPrioritizedTasks());
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