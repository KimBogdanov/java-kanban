package manager;

import exception.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public static final String FIRST_STRING = "id,type,name,status,description,epic";
    private File file;

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fbtm = new FileBackedTasksManager(file);
        ArrayList<String> stringList = readFileContents(file);
        stringList.remove(0);
        List<Integer> history = null;
        int maxId = 0;
        for (int i = 0; i < stringList.size(); i++) {
            if (stringList.get(i).isBlank()) {
                if (!stringList.get(++i).isEmpty()) {
                    history = CSVFormatter.historyFromString(stringList.get(i));
                }
                break;
            } else {
                Task task = CSVFormatter.fromString(stringList.get(i));
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
                fbtm.loadTask(task);
            }
        }
        if (history != null) {
            fbtm.loadHistory(history);
        }
        fbtm.setCounter(maxId);

        return fbtm;
    }

    private void loadTask(Task task) {
        if (task instanceof Epic) {
            epicDao.put(task.getId(), (Epic) task);
            System.out.println("Таск загружен, id= " + task.getId());
        } else if (task instanceof Subtask) {
            subtaskDao.put(task.getId(), (Subtask) task);
            System.out.println("Сабтаск загружен, id= " + task.getId());
        } else {
            taskDao.put(task.getId(), task);
            System.out.println("Эпик загружен, id= " + task.getId());
        }
    }

    private void loadHistory(List<Integer> history) {
        Stream.of(taskDao.values(), epicDao.values(), subtaskDao.values())
                .flatMap(Collection::stream)
                .filter(task -> history.contains(task.getId()))
                .forEach(historyManager::addTask);
    }

    private static ArrayList<String> readFileContents(File fileName) {
        try {
            return new ArrayList<>(Files.readAllLines(fileName.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Не считалось!");
        }
    }

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public Task saveTask(Task task) {
        super.saveTask(task);
        save();
        return task;
    }

    @Override
    public Subtask saveSubtask(Subtask subtask) {
        super.saveSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic saveEpic(Epic epic) {
        super.saveEpic(epic);
        save();
        return epic;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void updateStatusEpic(int epicId) {
        super.updateStatusEpic(epicId);
        save();
    }

    @Override
    protected void setCounter(int counter) {
        super.setCounter(counter);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void updateTask(Task taskNew) {
        super.updateTask(taskNew);
        save();
    }

    @Override
    public void updateEpic(Epic epicNew) {
        super.updateEpic(epicNew);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtaskNew) {
        super.updateSubtask(subtaskNew);
        save();
    }

    public void save() {
        String allTasksStringFormat = Stream.of(taskDao.values(), epicDao.values(), subtaskDao.values())
                .flatMap(Collection::stream)
                .map(CSVFormatter::toString)
                .collect(Collectors.joining("\n"));

        String history = CSVFormatter.historyToString(getHistory());

        try (Writer writer = new FileWriter(file)) {
            StringBuilder sb = new StringBuilder();
            sb.append(FIRST_STRING)
                    .append("\n")
                    .append(allTasksStringFormat)
                    .append("\n\n")
                    .append(history.isEmpty() ? "\n" : history);
            writer.write(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Не записалось!");
        }
    }
}
