package steal.app.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import steal.app.backend.league.LeagueDTO;
import steal.app.backend.player.PlayerDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeagueTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LeagueTests(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeAll
    void cleanupDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE leagues RESTART IDENTITY CASCADE;");
    }

    @Test
    @Transactional
    public void testCreateLeagueAndFetchById() throws Exception {
        LeagueDTO dto = new LeagueDTO();
        dto.setName("Unique League Name");
        dto.setOwnerId(1L);
        dto.setSport("Roundnet");
        dto.setLocation("Bolzano");
        String leagueJson = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Integer leagueId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get("/api/v1/leagues/{id}", leagueId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(leagueId))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.ownerId").value(dto.getOwnerId()))
                .andExpect(jsonPath("$.sport").value(dto.getSport()))
                .andExpect(jsonPath("$.location").value(dto.getLocation()));
    }

    @Test
    @Transactional
    public void testCreateLeagueAlreadyExists() throws Exception {
        LeagueDTO dto = new LeagueDTO();
        dto.setName("Unique League Name Redundant");
        dto.setOwnerId(1L);
        dto.setSport("Roundnet");
        dto.setLocation("Bolzano");
        String leagueJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueJson))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("League already exists"));
    }
}
