package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exception.HttpTaskServerException;
import manager.HttpTaskManager;
import manager.Managers;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;
    private final HttpTaskManager manager;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
        manager = Managers.getDefault();
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public void start() {
        System.out.println("Запускаем HttpTaskServer порт " + PORT);
        server.start();
    }

    public void stop(int delay) {
        System.out.println("Сервер c портом: " + PORT + " завершит работу через delay=" + delay);
        server.stop(delay);
    }

    private void handler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = getPath(exchange);
        String query = getQuery(exchange);
        try {
            switch (requestMethod) {
                case "GET":
                    if (pathParts.length == 2 && query == null) {
                        System.out.println("Получить приоритет тасок");
                        getPrioritizedTasks(exchange);
                        break;
                    }
                    if (pathParts.length == 3) {
                        switch (pathParts[2]) {
                            case "task":
                                if (query == null) {
                                    System.out.println("Получить все таски");
                                    getTasks(exchange);
                                } else {
                                    System.out.println("Получить таску");
                                    getTask(exchange);
                                }
                                break;
                            case "epic":
                                if (query == null) {
                                    System.out.println("Получить все эпики");
                                    getEpics(exchange);
                                } else {
                                    System.out.println("Получить эпик");
                                    getEpic(exchange);
                                }
                                break;
                            case "subtask":
                                if (query == null) {
                                    System.out.println("Получить все сабтаски");
                                    getSubtasks(exchange);
                                } else {
                                    System.out.println("Получить сабтаску");
                                    getSubtask(exchange);
                                }
                                break;
                            case "history":
                                System.out.println("Получить историю");
                                getHistory(exchange);
                                break;
                        }
                    }
                    if (pathParts.length == 4 && pathParts[2].equals("subtask")
                            && query != null && pathParts[3].equals("epic")) {
                        System.out.println("Получить сабтаски эпика");
                        getEpicSubtasksById(exchange);
                        break;
                    }
                    break;
                case "POST":
                    switch (pathParts[2]) {
                        case "task":
                            System.out.println("Post task");
                            postTask(exchange);
                            break;
                        case "epic":
                            System.out.println("Post epic");
                            postEpic(exchange);
                            break;
                        case "subtask":
                            System.out.println("Post subtask");
                            postSubtask(exchange);
                            break;
                    }
                    break;
                case "DELETE":
                    switch (pathParts[2]) {
                        case "task":
                            if (query == null) {
                                System.out.println("Удалить все Таски");
                                deleteAllTasks(exchange);
                            } else {
                                System.out.println("Удалить таску");
                                deleteTask(exchange);
                            }
                            break;
                        case "epic":
                            if (query == null) {
                                System.out.println("Удалить все эпики");
                                deleteAllEpics(exchange);
                            } else {
                                System.out.println("Удалить эпик");
                                deleteEpic(exchange);
                            }
                            break;
                        case "subtask":
                            if (query == null) {
                                System.out.println("Удалить все сабтаски");
                                deleteAllSubtask(exchange);
                            } else {
                                System.out.println("Удалить сабтаску");
                                deleteSubtask(exchange);
                            }
                            break;
                    }
                    break;
                default:
                    writeResponse(exchange, "Некорректный HTTP запрос, получили - "
                            + exchange.getRequestMethod() + exchange.getRequestURI().getPath(), 400);
                    System.out.println("Некорректный HTTP запрос, hendler");

            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка в Handler");
        } finally {
            exchange.close();
        }
    }

    private void getTasks(HttpExchange exchange) {
        try {
            List<Task> allTasks = manager.getAllTasks();
            if (!allTasks.isEmpty()) {
                String response = gson.toJson(allTasks);
                writeResponse(exchange, response, 200);
                System.out.println("Таски отправленны");
            } else {
                writeResponse(exchange, "Нет тасок", 200);
                System.out.println("Нет тасок");
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getTasks()");
        }
    }

    private void getTask(HttpExchange exchange) {
        try {
            int id = requestQueryId(exchange);
            if (manager.getTask(id) != null) {
                String resp = gson.toJson(manager.getTask(id));
                writeResponse(exchange, resp, 200);
                System.out.println("Task c id=" + id + " отправлен");
            } else {
                writeResponse(exchange, "Task c id=" + id + " не найдена", 404);
                System.out.println("Task c id=" + id + " не найдена");
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getTaskById()");
        }
    }

    private void postTask(HttpExchange exchange) throws IOException {
        String body = getBodyRequest(exchange);
        Task task = gson.fromJson(body, Task.class);
        if (task != null) {
            if (task.getId() != null) {
                if (manager.getTask(task.getId()) != null) {
                    manager.updateTask(task);
                    writeResponse(exchange, "Task обновлен", 200);
                    System.out.println("Task обновлен");
                } else {
                    writeResponse(exchange, "Task не обновлен, т.к. его нет в базе", 400);
                    System.out.println("Task не обновлен, т.к. его нет в базе");
                }
            } else {
                manager.saveTask(task);
                writeResponse(exchange, "Task добавлен", 200);
                System.out.println("Такс добавлен");
            }
        }
        writeResponse(exchange, "Передан неверный формат Task", 400);
    }

    private void deleteTask(HttpExchange exchange) {
        try {
            Integer idQuery = requestQueryId(exchange);
            if (manager.isContainsTask(idQuery)) {
                manager.deleteTask(idQuery);
                writeResponse(exchange, "Task c id=" + idQuery + " удален", 200);
            } else {
                writeResponse(exchange, "Task c id=" + idQuery + " не существует", 400);
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка при запросе deleteTaskById()");
        }
    }

    private void deleteAllTasks(HttpExchange exchange) {
        try {
            manager.deleteAllTasks();
            writeResponse(exchange, "Все task удалены", 200);
            System.out.println("Все task удалены");
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка deleteAllTasks()");
        }
    }

    private void getEpics(HttpExchange exchange) {
        try {
            List<Epic> allEpics = manager.getAllEpics();
            if (!allEpics.isEmpty()) {
                String resp = gson.toJson(allEpics);
                writeResponse(exchange, resp, 200);
                System.out.println("Epics  отправлены");
            } else {
                writeResponse(exchange, "Нет эпиков", 200);
                System.out.println("Нет эпиков");
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getEpics()");
        }
    }

    private void getEpic(HttpExchange exchange) {
        try {
            int idQuery = requestQueryId(exchange);
            if (manager.getEpic(idQuery) != null) {
                String resp = gson.toJson(manager.getEpic(idQuery));
                writeResponse(exchange, resp, 200);
                System.out.println("Epic c id=" + idQuery + " отправлен");
            } else {
                writeResponse(exchange, "Epic c id=" + idQuery + " не найден", 400);
                System.out.println("Epic c id=" + idQuery + " не найден");
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getEpicById()");
        }
    }

    private void postEpic(HttpExchange exchange) throws IOException {
        String body = getBodyRequest(exchange);
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic != null) {
            if (epic.getId() != null) {
                if (manager.getEpic(epic.getId()) != null) {
                    manager.updateEpic(epic);
                    writeResponse(exchange, "Epic обновлен", 200);
                } else {
                    writeResponse(exchange, "Epic не обновлен, т.к. не найден в базе", 400);
                }
            } else {
                manager.saveEpic(epic);
                writeResponse(exchange, "Epic Сохранен", 200);
            }
        }
        writeResponse(exchange, "Передан неверный формат Epic", 400);
    }

    private void deleteEpic(HttpExchange exchange) {
        try {
            int idQuery = requestQueryId(exchange);
            if (manager.isContainsEpic(idQuery)) {
                manager.deleteEpic(idQuery);
                writeResponse(exchange, "Epic c id=" + idQuery + " успешно удален", 200);
            } else {
                writeResponse(exchange, "Epic c id=" + idQuery + " не существует", 400);
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка при запросе deleteEpicById()");
        }
    }

    private void deleteAllEpics(HttpExchange exchange) {
        try {
            manager.deleteAllEpic();
            writeResponse(exchange, "Все epic удалены", 200);
            System.out.println("Все epic удалены");
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка deleteAllEpics()");
        }
    }

    private void getSubtasks(HttpExchange exchange) {
        try {
            List<Subtask> allSubtasks = manager.getAllSubtasks();
            if (!allSubtasks.isEmpty()) {
                String resp = gson.toJson(manager.getAllSubtasks());
                writeResponse(exchange, resp, 200);
                System.out.println("Список subtasks отправлен");
            } else {
                writeResponse(exchange, "Subtasks - пустой", 200);
                System.out.println("Список subtasks - пустой");
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getSubtasks()");
        }
    }

    private void getSubtask(HttpExchange exchange) {
        try {
            int idQuery = requestQueryId(exchange);
            if (manager.getSubtask(idQuery) != null) {
                String resp = gson.toJson(manager.getSubtask(idQuery));
                writeResponse(exchange, resp, 200);
                System.out.println("Subtask с id=" + idQuery + " отправлен");
            } else {
                writeResponse(exchange, "Subtask c id=" + idQuery + " не найден в базе", 400);
                System.out.println("Subtask c id=" + idQuery + " не найден");
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getSubtaskById()");
        }
    }

    private void deleteSubtask(HttpExchange exchange) {
        try {
            int idQuery = requestQueryId(exchange);
            if (manager.isContainsSubtask(idQuery)) {
                manager.deleteEpic(idQuery);
                writeResponse(exchange, "Subtask c id=" + idQuery + " удален", 200);
            } else {
                writeResponse(exchange, "Subtask c id=" + idQuery + " не существует", 400);
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка при запросе deleteSubtask()");
        }
    }

    private void deleteAllSubtask(HttpExchange exchange) {
        try {
            manager.deleteAllSubtask();
            writeResponse(exchange, "Все subtasks удалены", 200);
            System.out.println("Все subtasks удалены");
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка при запросе deleteAllSubtask()");
        }
    }

    private void getPrioritizedTasks(HttpExchange exchange) {
        try {
            if (!manager.getPrioritizedTasks().isEmpty()) {
                String resp = gson.toJson(manager.getPrioritizedTasks());
                writeResponse(exchange, resp, 200);
                System.out.println("Cписок приориета задач отправлен");
            } else {
                writeResponse(exchange, "Cписок приоритета пустой", 200);
                System.out.println("Cписок приоритета пустой");
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getPrioritizedTasks()");
        }
    }

    private void getEpicSubtasksById(HttpExchange exchange) {
        try {
            int idQuery = requestQueryId(exchange);
            Epic epic = manager.getEpic(idQuery);
            if (epic != null) {
                List<Subtask> subtaskForEpic = manager.getSubtaskForEpic(epic);
                if (!subtaskForEpic.isEmpty()) {
                    String resp = gson.toJson(subtaskForEpic);
                    writeResponse(exchange, resp, 200);
                    System.out.println("Список сабтасок эпика с id=" + idQuery + " отправлен");
                } else {
                    writeResponse(exchange, "Нет сабтасков у эпика id=" + idQuery + " пустой", 200);
                }
            } else {
                writeResponse(exchange, "Эпика id=" + idQuery + " не существует", 404);
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getEpicSubtasksById()");
        }
    }

    private void getHistory(HttpExchange exchange) {
        try {
            List<Task> history = manager.getHistory();
            if (!history.isEmpty()) {
                String resp = gson.toJson(history);
                writeResponse(exchange, resp, 200);
                System.out.println("История отправлена");
            } else {
                writeResponse(exchange, "История просмотров отсутствует", 200);
                System.out.println("История просмотров отсутствует");
            }
        } catch (IOException exp) {
            throw new HttpTaskServerException("Ошибка getHistory()");
        }
    }

    private void postSubtask(HttpExchange exchange) throws IOException {
        String body = getBodyRequest(exchange);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        if (subtask == null) {
            writeResponse(exchange, "Неверно введены данные", 400);
        } else {
            Integer epicId = subtask.getEpicId();
            if (epicId == null) {
                writeResponse(exchange, "Не указан epicId", 400);
            } else {
                if (manager.getEpic(epicId) != null) {
                    if (subtask.getId() == null) {
                        manager.saveSubtask(subtask);
                        writeResponse(exchange, "Сабтаска с id=" + subtask.getId() + ", сохранена", 200);
                    } else {
                        if (manager.getSubtask(subtask.getId()) != null) {
                            manager.updateSubtask(subtask);
                            writeResponse(exchange, "Сабтаска с id=" + subtask.getId() + ", обновлена", 200);
                        } else {
                            writeResponse(exchange, "Не удалось обновить." +
                                    "В базе нет сабтаски id=" + subtask.getId(), 400);
                        }
                    }
                } else {
                    writeResponse(exchange, "Не существует Эпика с epicId= " + subtask.getEpicId(), 400);
                    System.out.println("Не существует Эпика с epicId= " + subtask.getEpicId());
                }
            }
        }
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private String[] getPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }

    private String getQuery(HttpExchange exchange) {
        return exchange.getRequestURI().getQuery();
    }

    private Integer requestQueryId(HttpExchange exchange) {
        return Integer.parseInt(exchange.getRequestURI().getQuery().substring(3));
    }

    private String getBodyRequest(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    private JsonObject getJsonObject(HttpExchange exchange, String body) throws IOException {
        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
            writeResponse(exchange, "Переданное тело не JSON", 400);
        }
        return jsonElement.getAsJsonObject();
    }
}