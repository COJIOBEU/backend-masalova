package tracker;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final TrackerService service;

    public ProjectController(TrackerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request) {
        Project project = service.createProject(request.name());
        return ResponseEntity.status(201).body(project);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getProjects() {
        return ResponseEntity.ok(service.getProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        return ResponseEntity.ok(service.getProject(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable String id, @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(service.updateProject(id, request.name()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        service.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    public record ProjectRequest(String name) {}
}
