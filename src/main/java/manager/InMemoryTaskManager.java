package manager;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    Map<Integer, Task> taskDao = new HashMap<>();
    Map<Integer, Subtask> subtaskDao = new HashMap<>();
    Map<Integer, Epic> epicDao = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task saveTask(Task task) {
        if (task.getId() == null) {
            task.setId(counter++);
            taskDao.put(task.getId(), task);
            System.out.println("Таск сохранен, id= " + task.getId());
            return task;
        } else {
            System.out.println("Таск с id= " + task.getId() + " не был сохранен, т.к. id не равно null");
            return null;
        }
    }

    @Override
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
            System.out.println("Сабтаск с id= " + subtask.getId() + " не был сохранен, т.к. id не равно null");
            return null;
        }
    }

    @Override
    public Epic saveEpic(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(counter++);
            epicDao.put(epic.getId(), epic);
            System.out.println("Эпик сохранен, id= " + epic.getId());
            return epic;
        } else {
            System.out.println("Эпик с id= " + epic.getId() + " не был сохранен, т.к. id не равно null");
            return null;
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = taskDao.get(id);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtaskDao.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicDao.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskDao.values().stream().toList();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtaskDao.values().stream().toList();
    }

    @Override
    public List<Epic> getAllEpics() {
        return epicDao.values().stream().toList();
    }

    @Override
    public void deleteAllTasks() {
        taskDao.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Epic epic : epicDao.values()) {
            epic.getSubtaskList().clear();
        }
        subtaskDao.clear();
        epicDao.values().forEach(epic -> updateStatusEpic(epic.getId()));
    }

    @Override
    public void deleteAllEpic() {
        subtaskDao.clear();
        epicDao.clear();
    }

    @Override
    public void deleteTask(Integer id) {
        Task remove = taskDao.remove(id);
        if (historyManager.getHistory().contains(remove)) {
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(Integer id) {
        Epic epic = epicDao.get(subtaskDao.get(id).getEpicId());
        if (epic != null) {
            epic.getSubtaskList().remove(id);
            Subtask remove = subtaskDao.remove(id);
            if (historyManager.getHistory().contains(remove)) {
                historyManager.remove(id);
            }
            updateStatusEpic(epic.getId());
        }
    }

    @Override
    public void deleteEpic(Integer id) {
        List<Integer> subtaskList = epicDao.get(id).getSubtaskList();
//        subtaskList.forEach(this::deleteSubtask);
        for (Integer integer : subtaskList) {
            deleteSubtask(integer);
        }
        Epic remove = epicDao.remove(id);
        if (historyManager.getHistory().contains(remove)) {
            historyManager.remove(id);
        }
    }

    @Override
    public void updateTask(Task taskNew) {
        taskDao.put(taskNew.getId(), taskNew);
    }

    @Override
    public void updateEpic(Epic epicNew) {
        epicDao.put(epicNew.getId(), epicNew);
    }

    @Override
    public void updateSubtask(Subtask subtaskNew) {
        subtaskDao.put(subtaskNew.getEpicId(), subtaskNew);
        Epic epic = epicDao.get(subtaskNew.getEpicId());
        if (epic != null) {
            updateStatusEpic(epic.getId());
        }
    }

    @Override
    public List<Subtask> getSubtaskForEpic(Epic epic) {
        return epic.getSubtaskList().stream()
                .map(idSubtask -> subtaskDao.get(idSubtask))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatusEpic(int epicId) {
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}

