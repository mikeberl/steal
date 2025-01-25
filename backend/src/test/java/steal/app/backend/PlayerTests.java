package steal.app.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import steal.app.backend.player.PlayerDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    public void testCreatePlayer() throws Exception {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@gmail.com");
        dto.setPassword("password");
        String playerJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
