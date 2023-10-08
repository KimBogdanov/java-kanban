package manager;

import models.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVFormatter {
    private CSVFormatter() {
    }

    public static String toString(Task task) {
        String stringCSV = String.format("%s,%S,%s,%s,%s,",
                task.getId(),
                task.getClass().getSimpleName(),
                task.getName(),
                task.getStatus(),
                task.getDescription());
        if (task instanceof Subtask) {
            stringCSV += ((Subtask) task).getEpicId();
        }
        return stringCSV;
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        switch (TaskType.valueOf(fields[1])) {
            case TASK:
                return new Task(Integer.valueOf(fields[0]),
                        fields[2],
                        fields[4],
                        Status.valueOf(fields[3]));
            case SUBTASK:
                return new Subtask(Integer.valueOf(fields[0]),
                        fields[2],
                        fields[4],
                        Status.valueOf(fields[3]),
                        Integer.valueOf(fields[5]));
            case EPIC:
                return new Epic(Integer.valueOf(fields[0]),
                        fields[2],
                        fields[4],
                        Status.valueOf(fields[3]));
            default:
                return null;
        }
    }

    public static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public static List<Integer> historyFromString(String value) {
        String[] ids = value.split(",");
        return Arrays.stream(ids)
                .map(Integer::valueOf)
                .toList();
    }
}

