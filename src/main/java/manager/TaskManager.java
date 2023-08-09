package manager;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManager {
    private final AtomicInteger counter = new AtomicInteger(0);
    Map<Integer, Task> taskDao = new HashMap<>();
    Map<Integer, Subtask> subtaskDao = new HashMap<>();
    Map<Integer, Epic> epicDao = new HashMap<>();

    public void saveTask(Task task) {
        if (task.isNew()) {
            task.setId(counter.incrementAndGet());
        }
        taskDao.put(task.getId(), task);
        System.out.println("Таск сохранен");
    }

    public void saveSubtask(Subtask subtask) {
        Epic epic = epicDao.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такого эпика не существует");
            return;
        }
        if (subtask.isNew()) {
            subtask.setId(counter.incrementAndGet());
        }
        subtaskDao.put(subtask.getId(), subtask);
        if (!epic.getSubtaskList().contains(subtask.getId()))
            epic.addSubtaskId(subtask.getId());
        System.out.println("Сабтаск сохранен");
    }

    public void saveEpic(Epic epic) {
        if (epic.isNew()) {
            epic.setId(counter.incrementAndGet());
        }
        epicDao.put(epic.getId(), epic);
        System.out.println("Эпик сохранен");
    }

    public Task getTask(int id) {
        return taskDao.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtaskDao.get(id);
    }

    public Epic getEpic(int id) {
        return epicDao.get(id);
    }

    public Map<Integer, Task> getAllTasks() {
        return taskDao;
    }

    public Map<Integer, Subtask> getAllSubtasks() {
        return subtaskDao;
    }

    public Map<Integer, Epic> getAllEpics() {
        return epicDao;
    }

    public void deleteAllTasks() {
        taskDao.clear();
    }

    public void deleteAllSubtask() {
        subtaskDao.clear();
        if(!epicDao.isEmpty()){
            epicDao.forEach((Integer, Epic)-> Epic.setStatus(Status.NEW));
        }
    }

    public void deleteAllEpic() {
        subtaskDao.clear();
        epicDao.clear();
    }

    public void deleteTask(Integer id) {
        taskDao.remove(id);
    }

    public void deleteSubtask(Integer id) {
        subtaskDao.remove(id);
        Epic epic = epicDao.get(subtaskDao.get(id).getEpicId());
        if(epic != null){
            updateStatusEpic(epic.getId());
        }
    }

    public void deleteEpic(Integer id) {
        epicDao.get(id).getSubtaskList().forEach(this::deleteSubtask);
        epicDao.remove(id);
    }

    public void updateTask(Task taskNew) {
        taskDao.computeIfPresent(taskNew.getId(), (id, oldTask) -> taskNew);
    }

    public void updateEpic(Epic epicNew) {
        epicDao.computeIfPresent(epicNew.getId(), (id, oldEpic) -> epicNew);
    }

    public void updateSubtask(Subtask subtaskNew) {
        subtaskDao.computeIfPresent(subtaskNew.getEpicId(), (id, oldSubtask) -> subtaskNew);
        Epic epic = epicDao.get(subtaskNew.getEpicId());
        if(epic != null){
            updateStatusEpic(epic.getId());
        }
    }

    public Map<Integer, Subtask> getSubtaskForEpic(Epic epicNew) {
        Map<Integer, Subtask> subtasks = new HashMap<>();
        subtaskDao.values().stream()
                .filter(subtask -> epicNew.getSubtaskList().contains(subtask.getId()))
                .forEach(subtask -> subtasks.put(subtask.getId(), subtask));
        return subtasks;
    }

    private void updateStatusEpic(int epicId) {
        Epic epic = epicDao.get(epicId);
        List<Integer> subtaskList = epic.getSubtaskList();
        if (subtaskList.isEmpty()) {
            epic.setStatus(Status.NEW);
        }
        Status status = null;
        for (Integer subtaskId : subtaskList) {
            Subtask subtask = subtaskDao.get(subtaskId);
            if(status == null){
                status = subtask.getStatus();
                continue;
            }
            if(status.equals(subtask.getStatus())
            && !status.equals(Status.IN_PROGRESS)){
                continue;
            }
            epic.setStatus(Status.IN_PROGRESS);
        }
        epic.setStatus(status);
    }
}

