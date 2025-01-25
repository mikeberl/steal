package steal.app.backend.league;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/leagues")
public class LeagueController {

    private final LeagueService leagueService;

    @Autowired
    public LeagueController(final LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping
    public List<League> getAllLeagues() {
        return leagueService.getAllLeagues();
    }

    @GetMapping("{id}")
    public ResponseEntity<League> getLeagueById(@PathVariable Long id) {
        League league = leagueService.getLeagueById(id);
        return ResponseEntity.ok().body(league);
    }

    @PostMapping
    public ResponseEntity<?> createLeague(@Valid @RequestBody  LeagueDTO leagueDTO) {
        try {
            League savedLeague = leagueService.addLeague(LeagueMapper.toEntity(leagueDTO));
            return ResponseEntity.ok().body(savedLeague);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<League> updateLeague(@Valid @RequestBody  LeagueDTO leagueDTO) {
        League updatedLeague = leagueService.updateLeague(LeagueMapper.toEntity(leagueDTO));
        return ResponseEntity.ok().body(updatedLeague);
    }


}
