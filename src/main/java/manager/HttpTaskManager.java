package manager;

import server.KVClient;
import server.LocalDateTimeAdapter;
import com.google.gson.*;
import models.Epic;
import models.Subtask;
import models.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class HttpTaskManager extends FileBackedTasksManager {
    private static final String URL = "http://localhost:8078/";
    private final KVClient kvClient;
    private final Gson gson;

    public HttpTaskManager() {
        super();
        kvClient = new KVClient(URL);
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        load();
    }

    public void load() {
        String tasks = kvClient.load("task");
        int maxId = 0;
        if (tasks != null && !tasks.isEmpty()) {
            JsonArray jsonTaskArray = JsonParser.parseString(tasks).getAsJsonArray();
            for (JsonElement jsonTask : jsonTaskArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
                taskDao.put(task.getId(), task);
                addPriorityTasks(task);
            }
        }

        String subtasks = kvClient.load("subtask");
        if (subtasks != null && !subtasks.isEmpty()) {
            JsonArray jsonSubtaskArray = JsonParser.parseString(subtasks).getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtaskArray) {
                Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
                if (subtask.getId() > maxId) {
                    maxId = subtask.getId();
                }
                subtaskDao.put(subtask.getId(), subtask);
                addPriorityTasks(subtask);
            }
        }

        String epics = kvClient.load("epic");
        if (epics != null && !epics.isEmpty()) {
            JsonArray jsonEpicsArray = JsonParser.parseString(epics).getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic epic = gson.fromJson(jsonEpic, Epic.class);
                if (epic.getId() > maxId) {
                    maxId = epic.getId();
                }
                epicDao.put(epic.getId(), epic);
            }
        }
        setCounter(maxId);

        String history = kvClient.load("history");
        if (history != null && !history.isEmpty()) {
            JsonArray jsonHistoryArray = JsonParser.parseString(history).getAsJsonArray();
            List<Integer> ids = new ArrayList<>();
            for (JsonElement jsonTask : jsonHistoryArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                ids.add(task.getId());
            }
            loadHistory(ids);
        }
    }

    @Override
    public void save() {
        kvClient.put("task", gson.toJson(taskDao.values()));
        kvClient.put("subtask", gson.toJson(subtaskDao.values()));
        kvClient.put("epic", gson.toJson(epicDao.values()));
        kvClient.put("history", gson.toJson(getHistory()));
    }

}

