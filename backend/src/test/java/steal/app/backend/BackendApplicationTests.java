package steal.app.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import steal.app.backend.league.LeagueDTO;
import steal.app.backend.player.Player;
import steal.app.backend.player.PlayerDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BackendApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private static JdbcTemplate jdbcTemplate;

	@Autowired
	public BackendApplicationTests(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@BeforeAll
    static void cleanupDatabase() {
		jdbcTemplate.execute("TRUNCATE TABLE leagues RESTART IDENTITY CASCADE;");
		jdbcTemplate.execute("TRUNCATE TABLE players RESTART IDENTITY CASCADE;");
	}

	@Container
	private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	@BeforeEach
	void setup() {
		if (!postgres.isRunning()) {
			postgres.start();
		}
		System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
		System.setProperty("spring.datasource.username", postgres.getUsername());
		System.setProperty("spring.datasource.password", postgres.getPassword());
	}

	@Order(1)
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

	@Order(2)
	@Test
	public void testCreateLeague() throws Exception {
		LeagueDTO dto = new LeagueDTO();
		dto.setName("Unique League Name");
		dto.setOwnerId(1L);
		dto.setSport("Roundnet");
		dto.setLocation("Bolzano");
		String playerJson = objectMapper.writeValueAsString(dto);

		mockMvc.perform(post("/api/v1/leagues")
						.contentType(MediaType.APPLICATION_JSON)
						.content(playerJson))
				.andDo(print())
				.andExpect(status().isOk());
	}

//	@Test
//	public void testCreateMatch() {
//		// Prima crea un giocatore
//		var playerResponse = restTemplate.postForEntity("/api/players", new PlayerDTO("Jane Doe"), PlayerDTO.class);
//		assertEquals(201, playerResponse.getStatusCodeValue());
//
//		// Usa l'ID del giocatore per creare un match
//		var matchResponse = restTemplate.postForEntity("/api/matches", new MatchDTO(playerResponse.getBody().getId()), MatchDTO.class);
//		assertEquals(201, matchResponse.getStatusCodeValue());
//	}
}
