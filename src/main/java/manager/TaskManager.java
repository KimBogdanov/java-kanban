package manager;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskManager {
    private static int counter = 0;
    Map<Integer, Task> taskDao = new HashMap<>();
    Map<Integer, Subtask> subtaskDao = new HashMap<>();
    Map<Integer, Epic> epicDao = new HashMap<>();

    public Task saveTask(Task task) {
        if (task.getId() == null) {
            task.setId(counter++);
            taskDao.put(task.getId(), task);
            System.out.println("Таск сохранен, id= " + task.getId());
            return task;
        } else {
            System.out.println("Таск с id= " + task.getId() + " уже существует");
            return null;
        }
    }

    public Subtask saveSubtask(Subtask subtask) {
        if (subtask.getId() == null) {
            Epic epic = epicDao.get(subtask.getEpicId());
            if (epic == null) {
                System.out.println("Такого эпика не существует " + subtask.getId());
                return null;
            }
            subtask.setId(counter++);
            epic.getSubtaskList().add(subtask.getId());
            subtaskDao.put(subtask.getId(), subtask);
            updateStatusEpic(epic.getId());
            System.out.println("Сабтаск сохранен, id= " + subtask.getId());
            return subtask;
        } else {
            System.out.println("Сабтаск с id= " + subtask.getId() + " уже существует");
            return null;
        }
    }

    public Epic saveEpic(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(counter++);
            epicDao.put(epic.getId(), epic);
            System.out.println("Эпик сохранен, id= " + epic.getId());
            return epic;
        } else {
            System.out.println("Эпик с id= " + epic.getId() + " уже существует");
            return null;
        }
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

    public List<Task> getAllTasks() {
        return taskDao.values().stream().toList();
    }

    public List<Subtask> getAllSubtasks() {
        return subtaskDao.values().stream().toList();
    }

    public List<Epic> getAllEpics() {
        return epicDao.values().stream().toList();
    }

    public void deleteAllTasks() {
        taskDao.clear();
    }

    public void deleteAllSubtask() {
        for (Epic epic : epicDao.values()) {
            epic.getSubtaskList().clear();
        }
        subtaskDao.clear();
        epicDao.values().forEach(epic -> updateStatusEpic(epic.getId()));
    }

    public void deleteAllEpic() {
        subtaskDao.clear();
        epicDao.clear();
    }

    public void deleteTask(Integer id) {
        taskDao.remove(id);
    }

    public void deleteSubtask(Integer id) {
        Epic epic = epicDao.get(subtaskDao.get(id).getEpicId());
        if (epic != null) {
            epic.getSubtaskList().remove(id);
            subtaskDao.remove(id);
            updateStatusEpic(epic.getId());
        }
    }

    public void deleteEpic(Integer id) {
        epicDao.get(id).getSubtaskList().forEach(subTaskId -> subtaskDao.remove(subTaskId));
        epicDao.get(id).getSubtaskList().clear();
        epicDao.remove(id);
    }

    public void updateTask(Task taskNew) {
        taskDao.put(taskNew.getId(), taskNew);
    }

    public void updateEpic(Epic epicNew) {
        epicDao.put(epicNew.getId(), epicNew);
    }

    public void updateSubtask(Subtask subtaskNew) {
        subtaskDao.put(subtaskNew.getEpicId(), subtaskNew);
        Epic epic = epicDao.get(subtaskNew.getEpicId());
        if (epic != null) {
            updateStatusEpic(epic.getId());
        }
    }

    public List<Subtask> getSubtaskForEpic(Epic epic) {
        return epic.getSubtaskList().stream()
                .map(idSubtask -> subtaskDao.get(idSubtask))
                .collect(Collectors.toList());
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
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }
            if (status == subtask.getStatus()
                    && !(status == Status.IN_PROGRESS)) {
                continue;
            }
            epic.setStatus(Status.IN_PROGRESS);
        }
        epic.setStatus(status);
    }
}

