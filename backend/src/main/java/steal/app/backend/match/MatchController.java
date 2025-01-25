package steal.app.backend.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import steal.app.backend.services.MatchScoreService;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/matches")
public class MatchController {

    private final MatchService matchService;
    private final MatchScoreService matchScoreService;

    @Autowired
    public MatchController(final MatchService matchService, MatchScoreService matchScoreService) {
        this.matchService = matchService;
        this.matchScoreService = matchScoreService;
    }

    @GetMapping("by-id/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        Match match = matchService.findMatchById(id);
        return ResponseEntity.ok().body(match);
    }

    @GetMapping("by-league/{leagueId}")
    public ResponseEntity<List<Match>> getMatchesByLeague(@PathVariable Long leagueId) {
        List<Match> matches = this.matchService.getByLeague(leagueId);

        return ResponseEntity.ok().body(matches);
    }

    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody MatchDTO matchDTO) {
        Match match = this.matchScoreService.saveMatch(matchDTO);
        return ResponseEntity.ok().body(match);
    }

    @DeleteMapping("{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long matchId) {
        this.matchScoreService.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }

}
