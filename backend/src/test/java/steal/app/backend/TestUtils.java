package steal.app.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import steal.app.backend.league.LeagueDTO;
import steal.app.backend.player.PlayerDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TestUtils(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    /**
     * Esegue il login e restituisce il token JWT.
     *
     * @param email    l'email dell'utente
     * @param password la password dell'utente
     * @return il token JWT
     * @throws Exception se la chiamata fallisce
     */
    public String loginAndGetToken(String name, String password) throws Exception {
        String loginRequest = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, name, password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    /**
     * Crea un nuovo giocatore e restituisce la risposta.
     *
     * @param playerDTO il DTO del giocatore
     * @return la risposta del server
     * @throws Exception se la chiamata fallisce
     */
    public MvcResult createPlayer(PlayerDTO playerDTO) throws Exception {
        String playerJson = objectMapper.writeValueAsString(playerDTO);

        return mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andReturn();
    }

    public MvcResult createLeague(LeagueDTO leagueDTO, String authToken) throws Exception {
        String leagueJson = objectMapper.writeValueAsString(leagueDTO);

        return mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueJson)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();
    }


}

