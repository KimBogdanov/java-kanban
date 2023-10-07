package models;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer valueOf, String field, String field1, Status valueOf1) {
        super(valueOf, field, field1, valueOf1);
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
