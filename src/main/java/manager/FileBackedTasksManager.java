package manager;

import exception.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fbtm = new FileBackedTasksManager(file);
        ArrayList<String> stringList = readFileContents(file);
        stringList.remove(0);
        List<Integer> history = null;
        int maxId = 0;
        for (int i = 0; i < stringList.size(); i++) {
            if (stringList.get(i).isBlank()) {
                if (stringList.size() == i + 2) {
                    history = CSVFormatter.historyFromString(stringList.get(++i));
                }
                break;
            } else {
                Task task = CSVFormatter.fromString(stringList.get(i));
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
                if (task instanceof Epic) {
                    fbtm.updateEpic((Epic) task);
                }
                if (task instanceof Subtask) {
                    fbtm.updateSubtask((Subtask) task);
                } else {
                    fbtm.updateTask(task);
                }
            }
        }
        if (history != null) {
            fbtm.setCounter(maxId);
            for (Integer id : history) {
                if (fbtm.taskDao.containsKey(id)) {
                    fbtm.getTask(id);
                } else if (fbtm.subtaskDao.containsKey(id)) {
                    fbtm.getSubtask(id);
                } else if (fbtm.epicDao.containsKey(id)) {
                    fbtm.getEpic(id);
                }
            }
        }
        return fbtm;
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

    private void save() {
        List<String> allTasks = Stream.of(taskDao.values(), epicDao.values(), subtaskDao.values()).flatMap(Collection::stream).map(CSVFormatter::toString).toList();
        String allTasksStringFormat = String.join("\n", allTasks);
        String history = CSVFormatter.historyToString(getHistoryManager());

        try (Writer writer = new FileWriter(file)) {
            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n").append(allTasksStringFormat).append("\n\n").append(history);
            writer.write(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Не записалось!");
        }
    }
}
