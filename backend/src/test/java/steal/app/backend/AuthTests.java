package steal.app.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import steal.app.backend.player.Player;
import steal.app.backend.player.PlayerDTO;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TestUtils testUtils;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthTests(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeAll
     void cleanupDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE players RESTART IDENTITY CASCADE;");

    }

    @Test
    @Transactional
    public void testLogin() throws Exception {
        testUtils = new TestUtils(mockMvc, objectMapper);
        // Creazione di un nuovo giocatore
        PlayerDTO player = new PlayerDTO();
        player.setName("John Doe");
        player.setEmail("john.doe@gmail.com");
        player.setPassword("password");

        MvcResult mvcResult = testUtils.createPlayer(player);
        Player newPlayer = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Player.class);
        // Login e verifica del token JWT
        String token = testUtils.loginAndGetToken(newPlayer.getName(), newPlayer.getPassword());
        assertNotNull(token);
    }
}

