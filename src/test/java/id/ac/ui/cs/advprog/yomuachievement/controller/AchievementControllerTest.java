package id.ac.ui.cs.advprog.yomuachievement.controller;

import id.ac.ui.cs.advprog.yomuachievement.service.AchievementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AchievementController.class)
class AchievementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AchievementService achievementService;

    @Test
    void testUpdateAchievementProgress() throws Exception {
        String payload = """
                {
                  "userId": "user-123",
                  "achievementId": "550e8400-e29b-41d4-a716-446655440000"
                }
                """;

        mockMvc.perform(post("/api/internal/progress/achievement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Progres achievement berhasil diperbarui"));
    }

    @Test
    void testUpdateMissionProgress() throws Exception {
        String payload = """
                {
                  "userId": "user-123",
                  "missionId": "550e8400-e29b-41d4-a716-446655440000"
                }
                """;

        mockMvc.perform(post("/api/internal/progress/mission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Progres misi berhasil diperbarui"));
    }
}