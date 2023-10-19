package manager;


import Server.KVClient;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String URL = "http://localhost:8078/";
    private final KVClient kvClient;
    private final Gson gson;

    public HttpTaskManager() {
        super();
        kvClient = new KVClient(URL);
        gson = new GsonBuilder().serializeNulls().create();
        load();
    }

    private void load() {
        String tasks = kvClient.load("task");
        JsonElement jsonTasks = JsonParser.parseString(tasks);
        int maxId = 0;
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTaskArray = jsonTasks.getAsJsonArray();
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
        JsonElement jsonSubtasks = JsonParser.parseString(subtasks);
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtaskArray = jsonSubtasks.getAsJsonArray();
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
        JsonElement jsonEpics = JsonParser.parseString(epics);
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonSubtasks.getAsJsonArray();
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
        JsonElement jsonHistoryList = JsonParser.parseString(history);
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            List<Integer> ids = new ArrayList<>();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                ids.add(jsonTaskId.getAsInt());
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
