package steal.app.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import steal.app.backend.match.MatchDTO;
import steal.app.backend.player.Player;
import steal.app.backend.player.PlayerDTO;
import steal.app.backend.ranking.Ranking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MatchTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    private final JdbcTemplate jdbc;

    private TestUtils testUtils;

    @Autowired
    public MatchTests(final JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    Player winnerP = new Player();
    Player losingP = new Player();
    League league = new League();
    Ranking rankingL = new Ranking();
    Ranking rankingW = new Ranking();

    private String authToken;

    @BeforeAll
    void cleanupDatabase() {
        jdbc.execute("TRUNCATE TABLE leagues RESTART IDENTITY CASCADE;");
        jdbc.execute("TRUNCATE TABLE players RESTART IDENTITY CASCADE;");
        jdbc.execute("TRUNCATE TABLE rankings RESTART IDENTITY CASCADE;");
        jdbc.execute("TRUNCATE TABLE matches RESTART IDENTITY CASCADE;");
    }

    public Player getPlayer(final String name) throws Exception{
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName(name);
        playerDTO.setEmail("john.doe@gmail.com");
        playerDTO.setPassword("password");

        MvcResult playerResult = testUtils.createPlayer(playerDTO);
        return mapper.readValue(playerResult.getResponse().getContentAsString(), Player.class);
    }

    public League createLeague(final String name, Long ownerId) throws Exception {
        LeagueDTO leagueDTO = new LeagueDTO();
        leagueDTO.setName(name);
        leagueDTO.setOwnerId(ownerId);
        leagueDTO.setSport("Roundnet");
        leagueDTO.setLocation("Bolzano");
        MvcResult leaguesResult = testUtils.createLeague(leagueDTO, authToken);

        return mapper.readValue(leaguesResult.getResponse().getContentAsString(), League.class);
    }

    public Ranking registerToLeague(Long leagueId, Long playerId) throws Exception {
        MvcResult registrationResult = mvc.perform(post("/api/v1/registrations/" + leagueId + "/" + playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk()).andReturn();
        return mapper.readValue(registrationResult.getResponse().getContentAsString(), Ranking.class);
    }

    @BeforeAll
    void setup() throws Exception {
        testUtils = new TestUtils(mvc, mapper);
        try {
            winnerP = getPlayer("Mario Rossi");
            losingP = getPlayer("John Doe");
            authToken = testUtils.loginAndGetToken(winnerP.getName(), losingP.getPassword());
            league = createLeague("Test League", winnerP.getId());
            rankingL = registerToLeague(league.getId(), losingP.getId());
            rankingW = registerToLeague(league.getId(), winnerP.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Order(1)
    @Test
    public void createMatchTest() throws Exception {
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setLeagueId(league.getId());
        matchDTO.setCreatorId(winnerP.getId());
        matchDTO.setDate(LocalDate.now());
        matchDTO.setWinners(Collections.singletonList(winnerP.getId()));
        matchDTO.setLosers(Collections.singletonList(losingP.getId()));
        matchDTO.setWinnerPoints(10);
        matchDTO.setLoserPoints(-10);
        String matchJson = mapper.writeValueAsString(matchDTO);
        mvc.perform(post("/api/v1/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchJson)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.leagueId").value(matchDTO.getLeagueId()))
                .andExpect(jsonPath("$.creatorId").value(matchDTO.getCreatorId()))
                .andExpect(jsonPath("$.date").value(matchDTO.getDate().toString()))
                .andExpect(jsonPath("$.winners").exists())
                .andExpect(jsonPath("$.losers").exists())
                .andExpect(jsonPath("$.loserPoints").value(matchDTO.getLoserPoints()))
                .andExpect(jsonPath("$.winnerPoints").value(matchDTO.getWinnerPoints()));

        MvcResult rankingResult = mvc.perform(get("/api/v1/rankings/" + matchDTO.getLeagueId() + "/" + winnerP.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.leagueId").value(matchDTO.getLeagueId()))
                .andExpect(jsonPath("$.playerId").value(winnerP.getId()))
                .andExpect(jsonPath("$.totalScore").value(matchDTO.getWinnerPoints()))
                .andReturn();

        Ranking ranking = mapper.readValue(rankingResult.getResponse().getContentAsString(), Ranking.class);
        JsonNode rootNode = mapper.readTree(rankingResult.getResponse().getContentAsString());
        JsonNode matchIds = rootNode.get("matchIds");

        if (matchIds != null && matchIds.isArray()) {
            List<Long> matches = new ArrayList<>();
            for (JsonNode id : matchIds) {
                matches.add(id.asLong());
            }
            ranking.setMatchIds(matches);
        }

        if (ranking.getMatchIds().isEmpty()) {
            throw new Exception("Match is not saved in ranking");
        }
    }

    @Order(2)
    @Test
    public void updateMatchTest() throws Exception {
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setId(1L);
        matchDTO.setLeagueId(league.getId());
        matchDTO.setCreatorId(winnerP.getId());
        matchDTO.setDate(LocalDate.now());
        matchDTO.setWinners(Collections.singletonList(winnerP.getId()));
        matchDTO.setLosers(Collections.singletonList(losingP.getId()));
        matchDTO.setWinnerPoints(20);
        matchDTO.setLoserPoints(-20);
        String matchJson = mapper.writeValueAsString(matchDTO);
        mvc.perform(put("/api/v1/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchJson)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.leagueId").value(matchDTO.getLeagueId()))
                .andExpect(jsonPath("$.creatorId").value(matchDTO.getCreatorId()))
                .andExpect(jsonPath("$.date").value(matchDTO.getDate().toString()))
                .andExpect(jsonPath("$.winners").exists())
                .andExpect(jsonPath("$.losers").exists())
                .andExpect(jsonPath("$.loserPoints").value(matchDTO.getLoserPoints()))
                .andExpect(jsonPath("$.winnerPoints").value(matchDTO.getWinnerPoints()));

        MvcResult rankingResult = mvc.perform(get("/api/v1/rankings/" + matchDTO.getLeagueId() + "/" + winnerP.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.leagueId").value(matchDTO.getLeagueId()))
                .andExpect(jsonPath("$.playerId").value(winnerP.getId()))
                .andExpect(jsonPath("$.totalScore").value(matchDTO.getWinnerPoints()))
                .andReturn();

        Ranking ranking = mapper.readValue(rankingResult.getResponse().getContentAsString(), Ranking.class);
        JsonNode rootNode = mapper.readTree(rankingResult.getResponse().getContentAsString());
        JsonNode matchIds = rootNode.get("matchIds");

        if (matchIds != null && matchIds.isArray()) {
            List<Long> matches = new ArrayList<>();
            for (JsonNode id : matchIds) {
                matches.add(id.asLong());
            }
            ranking.setMatchIds(matches);
        }

        if (ranking.getMatchIds().isEmpty()) {
            throw new Exception("Match is not saved in ranking");
        }
    }

    @Order(3)
    @Test
    public void deleteMatchTest() throws Exception {
        mvc.perform(delete("/api/v1/matches/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        MvcResult rankingResult = mvc.perform(get("/api/v1/rankings/" + league.getId() + "/" + winnerP.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.leagueId").value(league.getId()))
                .andExpect(jsonPath("$.playerId").value(winnerP.getId()))
                .andReturn();

        Ranking ranking = mapper.readValue(rankingResult.getResponse().getContentAsString(), Ranking.class);
        JsonNode rootNode = mapper.readTree(rankingResult.getResponse().getContentAsString());
        JsonNode matchIds = rootNode.get("matchIds");

        if (matchIds != null && matchIds.isArray()) {
            List<Long> matches = new ArrayList<>();
            for (JsonNode id : matchIds) {
                matches.add(id.asLong());
            }
            ranking.setMatchIds(matches);
        }

        if (!ranking.getMatchIds().isEmpty()) {
            throw new Exception("Match is not removed from ranking");
        }
    }
}
