import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {

    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtasksId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        this.subtasksIds.remove(subtaskId);
    }

    public void clearSubtasks() {
        this.subtasksIds = subtasksIds;
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