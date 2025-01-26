package steal.app.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import steal.app.backend.league.League;
import steal.app.backend.league.LeagueDTO;
import steal.app.backend.player.Player;
import steal.app.backend.player.PlayerDTO;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    Player owner = new Player();

    @BeforeAll
    void cleanupDatabase() throws Exception {
        jdbcTemplate.execute("TRUNCATE TABLE leagues RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE players RESTART IDENTITY CASCADE;");
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk()).andReturn();

        this.owner = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Player.class);
    }

    @Test
    @Transactional
    public void testCreateLeagueAndFetchById() throws Exception {
        LeagueDTO dto = new LeagueDTO();
        dto.setName("Unique League Name");
        dto.setOwnerId(owner.getId());
        dto.setSport("Roundnet");
        dto.setLocation("Bolzano");
        String leagueJson = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.ownerId").value(dto.getOwnerId()))
                .andExpect(jsonPath("$.sport").value(dto.getSport()))
                .andExpect(jsonPath("$.location").value(dto.getLocation()))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Integer leagueId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get("/api/v1/leagues/{id}", leagueId)
                        .contentType(MediaType.APPLICATION_JSON))
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
        dto.setOwnerId(owner.getId());
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
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("League already exists"));
    }

    @Test
    @Transactional
    public void testCreateLeagueInvalidOwner() throws Exception {
        LeagueDTO dto = new LeagueDTO();
        dto.setName("Unique League Name Redundant");
        dto.setOwnerId(owner.getId() + 1);
        dto.setSport("Roundnet");
        dto.setLocation("Bolzano");
        String leagueJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Owner with id " + (owner.getId() + 1) + " does not exist"));
    }

    @Test
    @Transactional
    public void testCreateLeagueAndDelete() throws Exception {
        LeagueDTO dto = new LeagueDTO();
        dto.setName("Unique League Name");
        dto.setOwnerId(owner.getId());
        dto.setSport("Roundnet");
        dto.setLocation("Bolzano");
        String leagueJson = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.ownerId").value(dto.getOwnerId()))
                .andExpect(jsonPath("$.sport").value(dto.getSport()))
                .andExpect(jsonPath("$.location").value(dto.getLocation()))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Integer leagueId = JsonPath.read(responseBody, "$.id");

        MvcResult allLeaguesResult = mockMvc.perform(get("/api/v1/leagues")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String responseBody2 = allLeaguesResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<League> league = objectMapper.readValue(responseBody2, new TypeReference<List<League>>() {});

        if (league.size() != 1) {
            throw new Exception();
        }

        mockMvc.perform(delete("/api/v1/leagues/{id}", leagueId)
        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

         allLeaguesResult = mockMvc.perform(get("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
         responseBody2 = allLeaguesResult.getResponse().getContentAsString();
         objectMapper = new ObjectMapper();
         league = objectMapper.readValue(responseBody2, new TypeReference<List<League>>() {});

        if (!league.isEmpty()) {
            throw new Exception();
        }
    }
}
