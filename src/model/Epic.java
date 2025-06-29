package model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private LocalDateTime endTime;

    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        this.duration = Duration.ZERO;
    }

    public void updateTimeFields(List<Subtask> subtasks) {
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                LocalDateTime subStart = subtask.getStartTime();
                LocalDateTime subEnd = subtask.getEndTime();

                if (earliestStart == null || subStart.isBefore(earliestStart)) {
                    earliestStart = subStart;
                }
                if (latestEnd == null || (subEnd != null && subEnd.isAfter(latestEnd))) {
                    latestEnd = subEnd;
                }
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        this.startTime = earliestStart;
        this.endTime = latestEnd;
        this.duration = totalDuration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void addSubtasksId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        this.subtasksIds.remove(subtaskId);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtasksIds +
                ", id=" + getId() +
                ", name='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}