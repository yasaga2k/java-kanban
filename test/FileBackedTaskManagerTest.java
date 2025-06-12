import model.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtask().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasksWithHistory() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Test", "Desc", Status.NEW);
        manager.addTask(task);
        manager.getTaskById(task.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getHistory().size());
    }
}