package steal.app.backend.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/matches")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(final MatchService matchService) {
        this.matchService = matchService;
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

}
