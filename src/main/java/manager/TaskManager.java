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
    private int counter = 0;
    Map<Integer, Task> taskDao = new HashMap<>();
    Map<Integer, Subtask> subtaskDao = new HashMap<>();
    Map<Integer, Epic> epicDao = new HashMap<>();

    public void saveTask(Task task) {
        if (task.isNew()) {
//            Сохранять нужно только новые задачи - мы обязательно должны сами сгенерировать id,
//            иначе может получиться, что пользователь проставил и передал
//            некорректное id (например, которое уже занято),и работа приложения будет нарушена.
//            То же самое касается подзадачи и эпика
//      К сожалению, не понимаю, что именно исправить
//      У меня есть проверка на уникальность
//      id генерируется автоматически
//      Не могли бы вы перефразировать?
            task.setId(counter++);
        }
        taskDao.put(task.getId(), task);
        System.out.println("Таск сохранен, id= " + task.getId());
    }

    public void saveSubtask(Subtask subtask) {
        Epic epic = epicDao.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такого эпика не существует " + subtask.getId());
            return;
        }
        if (subtask.isNew()) {
            subtask.setId(counter++);
        }
        epic.getSubtaskList().add(subtask.getId());
        subtaskDao.put(subtask.getId(), subtask);
        updateStatusEpic(epic.getId());
        System.out.println("Сабтаск сохранен, id= " + subtask.getId());
    }

    public void saveEpic(Epic epic) {
        if (epic.isNew()) {
            epic.setId(counter++);
        }
        epicDao.put(epic.getId(), epic);
        System.out.println("Эпик сохранен, id= " + epic.getId());
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
        epicDao.values().forEach(epic -> updateStatusEpic(epic.getId()));
        for (Epic epic : epicDao.values()) {
            epic.getSubtaskList().clear();
        }
        subtaskDao.clear();
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
            updateStatusEpic(epic.getId());
            epic.getSubtaskList().remove(id);
            subtaskDao.remove(id);
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
        epicDao.computeIfPresent(epicNew.getId(), (id, oldEpic) -> epicNew);
    }

    public void updateSubtask(Subtask subtaskNew) {
        subtaskDao.computeIfPresent(subtaskNew.getEpicId(), (id, oldSubtask) -> subtaskNew);
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

