package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        taskType = TaskType.EPIC;
    }

    public Epic(Integer id, String name, String description, Status status, LocalDateTime startTime, Long duration) {
        super(id, name, description, status, startTime, duration);
        taskType = TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtaskList() {
        return subtaskIds;
    }

    public void setSubtaskList(List<Integer> subtaskList) {
        this.subtaskIds = subtaskList;
    }

    public void addSubtaskId(int subtuskId) {
        subtaskIds.add(subtuskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "Type " + this.getClass().getSimpleName() +
                ", subtaskList=" + subtaskIds.size() +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}
