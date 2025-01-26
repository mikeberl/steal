package steal.app.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import steal.app.backend.league.League;
import steal.app.backend.league.LeagueDTO;
import steal.app.backend.player.Player;
import steal.app.backend.player.PlayerDTO;
import steal.app.backend.ranking.Ranking;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrationsTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    private final JdbcTemplate jdbc;

    @Autowired
    public RegistrationsTests(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    Player owner = new Player();
    League league = new League();

    @BeforeAll
    void cleanupDatabase() throws Exception {
//        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        jdbc.execute("TRUNCATE TABLE leagues RESTART IDENTITY CASCADE;");
        jdbc.execute("TRUNCATE TABLE players RESTART IDENTITY CASCADE;");
        jdbc.execute("TRUNCATE TABLE rankings RESTART IDENTITY CASCADE;");

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName("John Doe");
        playerDTO.setEmail("john.doe@gmail.com");
        playerDTO.setPassword("password");
        String playerJson = mapper.writeValueAsString(playerDTO);

        MvcResult playerResult = mvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk()).andReturn();

        this.owner = mapper.readValue(playerResult.getResponse().getContentAsString(), Player.class);

        LeagueDTO leagueDTO = new LeagueDTO();
        leagueDTO.setName("Unique League Name");
        leagueDTO.setOwnerId(owner.getId());
        leagueDTO.setSport("Roundnet");
        leagueDTO.setLocation("Bolzano");
        String leagueJson = mapper.writeValueAsString(leagueDTO);

        MvcResult leaguesResult = mvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueJson))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        this.league = mapper.readValue(leaguesResult.getResponse().getContentAsString(), League.class);
    }

    @Order(1)
    @Test
    public void testRegisterToLeague() throws Exception {
        MvcResult rankingsResult = mvc.perform(get("/api/v1/rankings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String responseBody = rankingsResult.getResponse().getContentAsString();
        List<Ranking> rankings = mapper.readValue(responseBody, new TypeReference<List<Ranking>>() {
        });

        if (!rankings.isEmpty()) {
            throw new Exception("Wrong setup");
        }

        MvcResult registrationResult = mvc.perform(post("/api/v1/registrations/" + league.getId() + "/" + owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.leagueId").value(league.getId()))
                .andExpect(jsonPath("$.playerId").value(owner.getId()))
                .andExpect(jsonPath("$.matchIds").exists())
                .andExpect(jsonPath("$.totalScore").value(0))
                .andReturn();

        MvcResult playerResult = mvc.perform(get("/api/v1/players/" + owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = playerResult.getResponse().getContentAsString();
        Player playerAfterRegistration = mapper.readValue(jsonResponse, new TypeReference<Player>() {
        });
        JsonNode rootNode = mapper.readTree(jsonResponse);
        JsonNode leaguesNode = rootNode.get("leagues");

        if (leaguesNode != null && leaguesNode.isArray()) {
            List<Long> leagues = new ArrayList<>();
            for (JsonNode leagueNode : leaguesNode) {
                leagues.add(leagueNode.asLong());
            }
            playerAfterRegistration.setLeagues(leagues);
        }
        if (playerAfterRegistration.getLeagues().isEmpty()) {
            throw new Exception("Player did not register correctly");
        }

        MvcResult rankingsResultAfter = mvc.perform(get("/api/v1/rankings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String responseBodyAfter = rankingsResultAfter.getResponse().getContentAsString();
        List<Ranking> rankingsAfter = mapper.readValue(responseBodyAfter, new TypeReference<List<Ranking>>() {
        });

        if (rankingsAfter.isEmpty()) {
            throw new Exception("Ranking has not been created correctly");
        }

    }

    @Order(2)
    @Test
    public void testDeRegisterToLeague() throws Exception {
        MvcResult rankingsResult = mvc.perform(get("/api/v1/rankings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String responseBody = rankingsResult.getResponse().getContentAsString();
        List<Ranking> rankings = mapper.readValue(responseBody, new TypeReference<List<Ranking>>() {
        });

        if (rankings.isEmpty()) {
            throw new Exception("Wrong setup");
        }

        MvcResult registrationResult = mvc.perform(delete("/api/v1/registrations/" + league.getId() + "/" + owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();

        MvcResult playerResult = mvc.perform(get("/api/v1/players/" + owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = playerResult.getResponse().getContentAsString();
        Player playerAfterRegistration = mapper.readValue(jsonResponse, new TypeReference<Player>() {
        });
        JsonNode rootNode = mapper.readTree(jsonResponse);
        JsonNode leaguesNode = rootNode.get("leagues");

        if (leaguesNode != null && leaguesNode.isArray()) {
            List<Long> leagues = new ArrayList<>();
            for (JsonNode leagueNode : leaguesNode) {
                leagues.add(leagueNode.asLong());
            }
            playerAfterRegistration.setLeagues(leagues);
        }
        if (!playerAfterRegistration.getLeagues().isEmpty()) {
            throw new Exception("Player did not deregister correctly");
        }

        MvcResult rankingsResultAfter = mvc.perform(get("/api/v1/rankings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String responseBodyAfter = rankingsResultAfter.getResponse().getContentAsString();
        List<Ranking> rankingsAfter = mapper.readValue(responseBodyAfter, new TypeReference<List<Ranking>>() {
        });

        if (!rankingsAfter.isEmpty()) {
            throw new Exception("Ranking has not been deleted successfully");
        }
    }

    @Test
    public void testRegisterToLeagueInvalidPlayer() throws Exception {
        mvc.perform(post("/api/v1/registrations/" + league.getId() + "/" + (owner.getId() + 1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Player with id " + (owner.getId() + 1) + " does not exist"));
    }

    @Test
    public void testRegisterToLeagueInvalidLeague() throws Exception {
        mvc.perform(post("/api/v1/registrations/" + (league.getId() + 1) + "/" + (owner.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("League with id " + (league.getId() + 1) + " does not exist"));
    }

    @Test
    public void testDeRegisterToLeagueInvalidPlayer() throws Exception {
        mvc.perform(delete("/api/v1/registrations/" + league.getId() + "/" + (owner.getId() + 1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Player with id " + (owner.getId() + 1) + " does not exist"));
    }

    @Test
    public void testDeRegisterToLeagueInvalidLeague() throws Exception {
        mvc.perform(delete("/api/v1/registrations/" + (league.getId() + 1) + "/" + (owner.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("League with id " + (league.getId() + 1) + " does not exist"));
    }
}
