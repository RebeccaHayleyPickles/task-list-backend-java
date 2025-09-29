package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping(value = "/tasks", produces = "application/json")
    public ResponseEntity<List<Task>> getTaskList() {
        return ok(taskRepository.findAll());
    }

    @GetMapping(value = "/task/{id}", produces = "application/json")
    public ResponseEntity<Task> getTask(@PathVariable Integer id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskRepository.save(task);
        return ok(savedTask);
    }

    @PutMapping(value = "/task/{id}", produces = "application/json")
    public ResponseEntity<Task> updateTask(@PathVariable Integer id, @RequestBody Task task) {
        return taskRepository.findById(id)
            .map(existing -> {
                existing.setTitle(task.getTitle());
                existing.setDescription(task.getDescription());
                existing.setStatus(task.getStatus());
                existing.setDueDate(task.getDueDate());
                Task updated = taskRepository.save(existing);
                return ResponseEntity.ok(updated);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @DeleteMapping(value = "/task/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
