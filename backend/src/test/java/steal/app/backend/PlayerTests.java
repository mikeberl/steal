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
import steal.app.backend.player.PlayerDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PlayerTests(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeAll
    void cleanupDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE players RESTART IDENTITY CASCADE;");
    }

    @Test
    @Transactional
    public void testCreatePlayerValid() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testCreatePlayerEmptyName() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(jsonPath("$.message").value("Name cannot be empty"));
    }

    @Test
    @Transactional
    public void testCreatePlayerEmptyEmail() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(jsonPath("$.message").value("Email cannot be empty"));
    }

    @Test
    @Transactional
    public void testCreatePlayerEmptyPassword() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        String playerJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(jsonPath("$.message").value("Password cannot be empty"));
    }

    @Test
    @Transactional
    public void testCreatePlayerNoBody() throws Exception {
        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("JSON cannot be null or empty"));
    }

    @Test
    @Transactional
    public void testCreatePlayerPlayerAlreadyExist() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Player with name " + dto.getName() + " already exists"));
    }

    @Test
    @Transactional
    public void testGetPlayerByIdValid() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Integer playerId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get("/api/v1/players/" + playerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerId))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.password").value(dto.getPassword()));
    }

    @Test
    @Transactional
    public void testGetPlayerByNameValid() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        String playerName = JsonPath.read(responseBody, "$.name");
        Integer playerId = JsonPath.read(responseBody, "$.id");
        mockMvc.perform(get("/api/v1/players/byName").param("playerName", "John Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerId))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.password").value(dto.getPassword()));
    }

    @Test
    @Transactional
    public void testGetPlayerByIdInvalid() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Integer playerId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get("/api/v1/players/" + playerId + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testGetPlayerByNameInvalid() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        String playerName = JsonPath.read(responseBody, "$.name");

        mockMvc.perform(get("/api/v1/players/byName").param("playerName", playerName + "FAKE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testDeletePlayerValid() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Integer playerId = JsonPath.read(responseBody, "$.id");

        mockMvc.perform(delete("/api/v1/players/" + playerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

    }

    @Test
    @Transactional
    public void testDeletePlayerInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/players/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/v1/players/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }
}
