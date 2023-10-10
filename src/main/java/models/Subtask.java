package models;

public class Subtask extends Task {
    Integer epicId;

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
        taskType = TaskType.SUBTASK;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status);
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
