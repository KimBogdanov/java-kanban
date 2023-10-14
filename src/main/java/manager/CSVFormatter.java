package manager;

import models.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVFormatter {
    private CSVFormatter() {
    }

    public static String toString(Task task) {
        String stringCSV = String.format("%s,%S,%s,%s,%s,%s,%s",
                task.getId(),
                task.getTaskType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime(),
                task.getDuration());
        if (task instanceof Subtask) {
            stringCSV += ((Subtask) task).getEpicId();
        }
        return stringCSV;
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        TaskType taskType = TaskType.valueOf(fields[1]);
        Integer id = Integer.valueOf(fields[0]);
        String name = fields[2];
        String description = fields[4];
        Status status = Status.valueOf(fields[3]);
        LocalDateTime startTime = LocalDateTime.parse(fields[5]);
        long duration = Long.parseLong(fields[6]);
        switch (taskType) {
            case TASK:
                return new Task(id, name, description, status, startTime, duration);
            case SUBTASK:
                Integer epicId = Integer.valueOf(fields[5]);
                return new Subtask(id, name, description, status, startTime, duration, epicId);
            case EPIC:
                return new Epic(id, name, description, status, startTime, duration);
            default:
                return null;
        }
    }

    public static String historyToString(List<Task> history) {
        return history.stream()
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

