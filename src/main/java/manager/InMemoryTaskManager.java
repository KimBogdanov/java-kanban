package manager;

import exception.ManagerValidateException;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    protected Map<Integer, Task> taskDao = new HashMap<>();
    protected Map<Integer, Subtask> subtaskDao = new HashMap<>();
    protected Map<Integer, Epic> epicDao = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected Set<Task> priorityTasksSet = new TreeSet<>(taskComparator);

    protected void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public Task saveTask(Task task) {
        if (task == null) {
            return null;
        }
        if (task.getId() == null) {
            task.setId(counter++);
            addPriority(task);
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
        if (subtask != null) {
            if (subtask.getId() == null) {
                Epic epic = epicDao.get(subtask.getEpicId());
                if (epic == null) {
                    System.out.println("Такого эпика не существует " + subtask.getId());
                    return null;
                }
                subtask.setId(counter++);
                addPriority(subtask);
                epic.getSubtaskList().add(subtask.getId());
                subtaskDao.put(subtask.getId(), subtask);
                updateStatusEpic(epic.getId());
                System.out.println("Сабтаск сохранен, id= " + subtask.getId());
                calculateEpicTime(epic);
                return subtask;
            } else {
                System.out.println("Сабтаск с id= " + subtask.getId() + " не был сохранен, т.к. id не равно null");
                return null;
            }
        }
        return null;
    }

    @Override
    public Epic saveEpic(Epic epic) {
        if (epic != null) {
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
        return null;

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
    public void deleteTask(Integer id) {
        if (taskDao.containsKey(id)) {
            priorityTasksSet.removeIf(task -> task.getId().equals(id));
            taskDao.remove(id);
            historyManager.remove(id);
        }
    }

    public void deleteAllTasks() {
        if (!taskDao.isEmpty()) {
            new ArrayList<>(taskDao.keySet()).forEach(this::deleteTask);
            System.out.println("Все таски удалены");
        } else {
            System.out.println("Невозможно удалить. Нет сохраненных тасок");
        }
    }

    @Override
    public void deleteEpic(Integer id) {
        Epic epic = epicDao.get(id);
        if (epic != null) {
            new ArrayList<>(epic.getSubtaskList()).forEach(this::deleteSubtask);
        }
        Epic remove = epicDao.remove(id);
        if (remove != null) {
            System.out.println("Epic id=" + id + " has been deleted");
            if (historyManager.contains(id)) {
                historyManager.remove(id);
            }
        }
    }

    @Override
    public void deleteAllEpic() {
        if (!epicDao.isEmpty()) {
            new ArrayList<>(epicDao.keySet()).forEach(this::deleteEpic);
            System.out.println("Все эпики удалены");
        } else {
            System.out.println("Невозможно удалить. Нет сохраненных эпиков");
        }
    }

    @Override
    public void deleteSubtask(Integer id) {
        Epic epic = epicDao.get(subtaskDao.get(id).getEpicId());
        if (epic != null) {
            epic.getSubtaskList().remove(id);
            Subtask remove = subtaskDao.remove(id);
            priorityTasksSet.remove(remove);
            System.out.println("Subtask id=" + id + " has been deleted");
            if (historyManager.contains(id)) {
                historyManager.remove(id);
            }
            updateStatusEpic(epic.getId());
            calculateEpicTime(epic);
        }
    }

    @Override
    public void deleteAllSubtask() {
        if (!subtaskDao.isEmpty()) {
            new HashSet<>(subtaskDao.keySet()).forEach(this::deleteSubtask);
            System.out.println("Все сабтаски удалены");
        } else {
            System.out.println("Невозможно удалить. Нет сохраненных сабтасок");
        }
    }

    @Override
    public void updateTask(Task taskNew) {
            if (taskNew != null && taskDao.containsKey(taskNew.getId())) {
                addPriority(taskNew);
                taskDao.put(taskNew.getId(), taskNew);
            } else {
                System.out.println("Таск не обновлен");
            }

    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epicDao.containsKey(epic.getId())) {
            epicDao.put(epic.getId(), epic);
            updateStatusEpic(epic.getId());
            calculateEpicTime(epic);
        } else {
            System.out.println("Эпик не сохранен");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtaskDao.containsKey(subtask.getId())) {
            addPriority(subtask);
            subtaskDao.put(subtask.getId(), subtask);
            Epic epic = epicDao.get(subtask.getEpicId());
            updateStatusEpic(epic.getId());
            calculateEpicTime(epic);
        } else {
            System.out.println("Subtask not found");
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

    public void calculateEpicTime(Epic epic) {
        List<Integer> subtaskList = epic.getSubtaskList();
        if (!subtaskList.isEmpty()) {
            List<Subtask> subtasks = subtaskList.stream()
                    .map(id -> subtaskDao.get(id))
                    .toList();
            LocalDateTime startTime = subtasks.get(0).getStartTime();
            LocalDateTime endTime = subtasks.get(0).getEndTime();
            for (Subtask subtask : subtasks) {
                if (subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
                if (subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
            }
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(ChronoUnit.MINUTES.between(startTime, endTime));
        } else {
            System.out.println("Чтобы вычислить endTime, добавьте подзадачи");
        }
    }

    private void addPriority(Task task) {
        priorityTasksSet.add(task);
        validateTaskPriority();
    }

    public boolean checkCrossTime(Task task) {
        long count = priorityTasksSet.stream()
                .filter(taskSave -> taskSave.getStartTime() != null && taskSave.getEndTime() != null)
                .filter(taskSave ->
                        (task.getStartTime().isBefore(taskSave.getStartTime())
                                && task.getEndTime().isBefore(taskSave.getStartTime()))
                                ||
                                (task.getStartTime().isAfter(taskSave.getEndTime())
                                        && task.getEndTime().isAfter(taskSave.getEndTime()))
                )
                .count();

        return (count > 0) || (priorityTasksSet.size() == count);
    }

    private void validateTaskPriority() {
        List<Task> tasks = getPriorityTasksSet();

        for (int i = 1; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean taskHasIntersections = checkCrossTime(task);
            if (taskHasIntersections) {
                throw new ManagerValidateException(
                        "Задачи #" + task.getId() + " и #" + tasks.get(i - 1) + "пересекаются");
            }
        }
    }

    private List<Task> getPriorityTasksSet() {
        return priorityTasksSet.stream().toList();
    }
}

