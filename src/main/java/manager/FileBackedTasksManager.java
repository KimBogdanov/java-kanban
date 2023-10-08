package manager;

import exception.ManagerSaveException;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public static FileBackedTasksManager loadFromFile(File file) {
        return null;
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
        List<String> allTasks = Stream.of(taskDao.values(), epicDao.values(), subtaskDao.values())
                .flatMap(Collection::stream)
                .map(CSVFormatter::toString)
                .toList();
        String allTasksStringFormat = String.join("\n", allTasks);
        String history = CSVFormatter.historyToString(getHistoryManager());

        try (Writer writer = new FileWriter(file)) {
            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n")
                    .append(allTasksStringFormat)
                    .append("\n\n")
                    .append(history);
            writer.write(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Полундра, все пропало!");
        }
    }
}
