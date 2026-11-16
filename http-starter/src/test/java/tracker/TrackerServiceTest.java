package tracker;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrackerServiceTest {

    private TrackerService service;

    @BeforeEach
    void setUp() {
        service = new TrackerService();
    }

    @Test
    void shouldCreateProjectSuccessfully() {
        System.out.println("Creating project");
        Project project = service.createProject("Test Project");

        System.out.println("Project created. Name: " + project.getName() + ", ID: " + project.getId());

        assertNotNull(project);
        assertEquals("Test Project", project.getName());
        assertNotNull(project.getId());
    }

    @Test
    void shouldThrowExceptionWhenProjectNameIsEmpty() {
        System.out.println("Validating empty name");
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createProject("   ")
        );

        System.out.println("Error message: " + exception.getMessage());
        assertNotNull(exception.getMessage());
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        System.out.println("Creating task");
        Project project = service.createProject("Project for Task");
        System.out.println("Project created with ID: " + project.getId());
        
        Task task = service.createTask(
                "Fix bug", 
                project.getId(), 
                "BugTask", 
                "High", 
                5
        );

        System.out.println("Task created. Title: " + task.getTitle() + ", Type: " + task.getType());
        
        assertNotNull(task);
        assertEquals("Fix bug", task.getTitle());
        assertEquals(project.getId(), task.getProjectId());
    }

    @Test
    void shouldThrowExceptionWhenCompletingNonExistentTask() {
        System.out.println("Completing non-existent task");
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.completeTask("fake-id-123")
        );

        System.out.println("Error message: " + exception.getMessage());
        assertNotNull(exception.getMessage());
    }

    @Test
    void shouldReturnTasksFilteredByProjectId() {
        System.out.println("Filtering tasks");
        Project project1 = service.createProject("Project 1");
        Project project2 = service.createProject("Project 2");

        service.createTask("Task 1", project1.getId(), "BugTask", "Low", 1);
        service.createTask("Task 2", project2.getId(), "FeatureTask", "Medium", 3);
        service.createTask("Task 3", project1.getId(), "BugTask", "High", 2);

        List<Task> tasksForProject1 = service.getTasks(project1.getId(), null, null);

        System.out.println("Tasks found for Project 1: " + tasksForProject1.size());
        
        for (Task t : tasksForProject1) {
            System.out.println(" - " + t.getTitle());
        }

        assertEquals(2, tasksForProject1.size());
    }
}