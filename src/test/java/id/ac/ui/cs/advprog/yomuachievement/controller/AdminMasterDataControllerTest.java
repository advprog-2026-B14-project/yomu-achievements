package id.ac.ui.cs.advprog.yomuachievement.controller;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.service.MasterDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AdminMasterDataController.class)
class AdminMasterDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MasterDataService masterDataService;

    @Test
    void testAddAchievement_ReturnsOk() throws Exception {
        Achievement mockAchievement = new Achievement();
        mockAchievement.setNama("Test Controller");
        mockAchievement.setMilestoneType("kuis");
        mockAchievement.setMilestoneTarget(5);

        when(masterDataService.createAchievement(any(Achievement.class))).thenReturn(mockAchievement);

        String jsonPayload = """
                {
                  "nama": "Test Controller",
                  "deskripsi": "Deskripsi test",
                  "milestoneTarget": 5,
                  "poinReward": 10,
                  "milestoneType": "kuis"
                }
                """;

        mockMvc.perform(post("/api/admin/master/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("Test Controller")); // Validasi balasan JSON
    }

    @Test
    void testAddDailyMission_ReturnsOk() throws Exception {
        id.ac.ui.cs.advprog.yomuachievement.model.DailyMission mockMission = new id.ac.ui.cs.advprog.yomuachievement.model.DailyMission();
        mockMission.setNamaMisi("Misi Login");
        mockMission.setMilestoneTarget(1);
        mockMission.setPoinReward(5);

        when(masterDataService.createDailyMission(any(id.ac.ui.cs.advprog.yomuachievement.model.DailyMission.class))).thenReturn(mockMission);

        String jsonPayload = """
                {
                  "namaMisi": "Misi Login",
                  "milestoneTarget": 1,
                  "poinReward": 5
                }
                """;

        mockMvc.perform(post("/api/admin/master/missions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.namaMisi").value("Misi Login"));
    }
}