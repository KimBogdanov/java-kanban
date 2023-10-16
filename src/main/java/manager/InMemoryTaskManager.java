package manager;

import exception.ManagerValidateException;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    protected Map<Integer, Task> taskDao = new HashMap<>();
    protected Map<Integer, Subtask> subtaskDao = new HashMap<>();
    protected Map<Integer, Epic> epicDao = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private final Comparator<Task> taskComparator = Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()));
    protected Set<Task> priorityTasks = new TreeSet<>(taskComparator);

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
            addPriorityTasks(task);
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
                addPriorityTasks(subtask);
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
            priorityTasks.removeIf(task -> task.getId().equals(id));
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
            priorityTasks.remove(remove);
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
            priorityTasks.remove(taskNew);
            addPriorityTasks(taskNew);
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
            priorityTasks.remove(subtask);
            addPriorityTasks(subtask);
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

    // Получился плохо читаемый код, стремился сделать в один проход по сабтаскам.
    // Можно сделать через Stream, но тогда по списку придется пройти три раза,
    // или делать отдельный класс для reduce. Пытялся прикрутить optional, читаемости не помогло
    public void calculateEpicTime(Epic epic) {
        List<Integer> subtaskList = epic.getSubtaskList();
        if (!subtaskList.isEmpty()) {
            List<Subtask> subtasks = getSubtaskForEpic(epic);
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            Long duration = null;
            for (Subtask subtask : subtasks) {
                if (subtask.getStartTime() != null) {
                    if (startTime == null) {
                        startTime = subtask.getStartTime();
                    } else if (subtask.getStartTime().isBefore(startTime)) {
                        startTime = subtask.getStartTime();
                    }
                    if (subtask.getDuration() != null) {
                        if (duration == null) {
                            duration = subtask.getDuration();
                        } else {
                            duration += subtask.getDuration();
                        }
                        if (endTime == null) {
                            endTime = subtask.getEndTime();
                        } else if (subtask.getEndTime().isAfter(endTime)) {
                            endTime = subtask.getEndTime();
                        }
                    }
                }
            }
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);
        } else {
            System.out.println("Чтобы вычислить endTime, добавьте подзадачи");
        }
    }

    public void addPriorityTasks(Task task) {
        if (task.getStartTime() == null || priorityTasks.isEmpty()) {
            priorityTasks.add(task);
        } else if (!checkCrossTime(task)) {
            priorityTasks.add(task);
        } else {
            throw new ManagerValidateException(
                    "Нельзя сохранить " + task.getTaskType() + " №"
                            + task.getId() + " т.к. он пересекается с другой задачей");
        }
    }

    private boolean checkCrossTime(Task task) {
        List<Task> prioritizedTasks = getPrioritizedTasks();
        LocalDateTime startTimeTask = task.getStartTime();
        LocalDateTime endTimeTask = task.getEndTime();

        for (Task prioritizedTask : prioritizedTasks) {
            LocalDateTime startTime = prioritizedTask.getStartTime();
            LocalDateTime endTime = prioritizedTask.getEndTime();
            if (startTime != null && endTime != null) {
                if ((startTimeTask.isAfter(startTime) && startTimeTask.isBefore(endTime)) ||
                        (endTimeTask.isAfter(startTime) && startTimeTask.isBefore(endTime)) ||
                        (startTimeTask.isBefore(startTime) && endTimeTask.isAfter(endTime))) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Task> getPrioritizedTasks() {
        return priorityTasks.stream().toList();
    }
}

