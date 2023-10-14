package models;

import java.time.LocalDateTime;

public class Subtask extends Task {
    Integer epicId;

    public Subtask(String name, String description, LocalDateTime startTime, long duration, Integer epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        taskType = TaskType.SUBTASK;
    }

    public Subtask(Integer id, String name, String description, Status status,
                   LocalDateTime startTime, long duration, Integer epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
        taskType = TaskType.SUBTASK;
    }


    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "Type " + this.getClass().getSimpleName() +
                ", epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}


