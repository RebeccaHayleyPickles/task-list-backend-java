package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Resource
    private MockMvc mockMvc;

    @MockitoBean
    private TaskRepository taskRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listTasks_shouldReturnListOfTasks() throws Exception {
        Task t1 = new Task(1, "One", "Desc1", "NotStarted", LocalDate.of(2025, 1, 1));
        Task t2 = new Task(2, "Two", "Desc2", "InProgress", LocalDate.of(2025, 2, 2));
        Mockito.when(taskRepository.findAll()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].title", is("One")))
            .andExpect(jsonPath("$[0].status", is("NotStarted")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].status", is("InProgress")));

    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        Task t1 = new Task(42, "One", "Desc1", "NotStarted", LocalDate.of(2025, 1, 1));
        Mockito.when(taskRepository.findById(42)).thenReturn(Optional.of(t1));

        mockMvc.perform(get("/task/{id}", 42))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(42)))
            .andExpect(jsonPath("$.title", is("One")))
            .andExpect(jsonPath("$.description", is("Desc1")))
            .andExpect(jsonPath("$.status", is("NotStarted")))
            .andExpect(jsonPath("$.dueDate", is("2025-01-01")));
    }

    @Test
    void getTaskById_shouldReturnNotFound() throws Exception {
        Mockito.when(taskRepository.findById(42)).thenReturn(Optional.empty());

        mockMvc.perform(get("/task/{id}", 42))
            .andExpect(status().isNotFound());
    }

    @Test
    void createTask_shouldReturnTask() throws Exception {
        Task task = new Task(1, "New Task", "Description", "NotStarted", LocalDate.of(2025, 1, 1));
        Mockito.when(taskRepository.save(any())).thenReturn(task);

        mockMvc.perform(post("/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("New Task")))
            .andExpect(jsonPath("$.description", is("Description")))
            .andExpect(jsonPath("$.status", is("NotStarted")))
            .andExpect(jsonPath("$.dueDate", is("2025-01-01")));
    }

    @Test
    void updateTask_shouldReturnTask() throws Exception {
        Task existingTask = new Task(1, "Existing Task", "Description", "NotStarted", LocalDate.of(2025, 1, 1));
        Mockito.when(taskRepository.findById(1)).thenReturn(Optional.of(existingTask));

        Task updatedTask = new Task(1, "Updated Task", "New Description", "InProgress", LocalDate.of(2026, 2, 2));
        Mockito.when(taskRepository.save(any())).thenReturn(updatedTask);

        mockMvc.perform(put("/task/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTask)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Updated Task")))
            .andExpect(jsonPath("$.description", is("New Description")))
            .andExpect(jsonPath("$.status", is("InProgress")))
            .andExpect(jsonPath("$.dueDate", is("2026-02-02")));
    }

    @Test
    void updateTask_shouldReturnNotFound() throws Exception {
        Mockito.when(taskRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(put("/task/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new Task(1, "Updated Task", "New Description", "InProgress", LocalDate.of(2026, 2, 2)))))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        Mockito.when(taskRepository.existsById(1)).thenReturn(true);
        Mockito.doNothing().when(taskRepository).deleteById(1);
        mockMvc.perform(delete("/task/{id}", 1))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_shouldReturnNotFound() throws Exception {
        Mockito.when(taskRepository.existsById(1)).thenReturn(false);
        mockMvc.perform(delete("/task/{id}", 1))
            .andExpect(status().isNotFound());
    }
}
