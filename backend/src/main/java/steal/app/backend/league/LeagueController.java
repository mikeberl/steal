package steal.app.backend.league;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        League savedLeague = leagueService.addLeague(LeagueMapper.toEntity(leagueDTO));
        return ResponseEntity.ok().body(savedLeague);
    }

    @PutMapping
    public ResponseEntity<League> updateLeague(@Valid @RequestBody  LeagueDTO leagueDTO) {
        League updatedLeague = leagueService.updateLeague(LeagueMapper.toEntity(leagueDTO));
        return ResponseEntity.ok().body(updatedLeague);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteLeague(@PathVariable Long id) {
        leagueService.deleteLeague(id);
        return ResponseEntity.noContent().build();
    }


}
