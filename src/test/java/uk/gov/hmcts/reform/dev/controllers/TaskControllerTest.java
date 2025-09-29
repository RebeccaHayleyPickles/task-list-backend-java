package uk.gov.hmcts.reform.dev.controllers;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.enums.TaskStatus;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

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

    @Test
    void listTasks_shouldReturnListOfTasks() throws Exception {
        Task t1 = new Task(1, "One", "Desc1", TaskStatus.NotStarted, LocalDateTime.of(2025, 1, 1, 9, 0));
        Task t2 = new Task(2, "Two", "Desc2", TaskStatus.InProgress, LocalDateTime.of(2025, 2, 2, 10, 0));
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
}
