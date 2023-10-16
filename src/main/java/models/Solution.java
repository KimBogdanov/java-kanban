package models;

import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.stream;

public class Solution {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic", "Desc");
        manager.saveEpic(epic);
        Subtask subtask = new Subtask("Sub", "Desc", epic.getId());
        manager.saveSubtask(new Subtask(null, "Sub",
                "Desc",  Status.NEW,LocalDateTime.of(2020,2,2,2,2),50L,epic.getId()));

        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(50L);

        manager.saveSubtask(subtask);
        System.out.println(epic.getDuration());
        System.out.println(LocalDateTime.now());
        System.out.println(epic.getEndTime());
//        System.out.println(epic.startTime);

    }
}
