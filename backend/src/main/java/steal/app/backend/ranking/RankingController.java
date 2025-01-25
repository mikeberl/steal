package steal.app.backend.ranking;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/rankings")
public class RankingController {

    private final RankingService rankingService;
    @Autowired
    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public List<Ranking> getRankings() {
        return rankingService.getRankings();
    }

    @GetMapping("{rankingId}")
    public Ranking getRankingById(@PathVariable("rankingId") Long rankingId) {
        return rankingService.getRankingById(rankingId);
    }

    @GetMapping("by-league/{leagueId}")
    public ResponseEntity<List<Ranking>> getRankingsByLeague(@PathVariable("leagueId") Long leagueId) {
        List<Ranking> rankingsOfLeague = rankingService.getRankingByLeagueId(leagueId);
        return ResponseEntity.ok().body(rankingsOfLeague);
    }

    @GetMapping("by-league-complete/{leagueId}")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingsByLeagueComplete(@PathVariable("leagueId") Long leagueId) {
        List<PlayerRankingDTO> rankingsWithInfo = rankingService.getPlayerRankingsForLeague(leagueId);
        return ResponseEntity.ok().body(rankingsWithInfo);
    }

    @GetMapping("by-player/{playerId}")
    public ResponseEntity<List<Ranking>> getRankingsByPlayer(@PathVariable("playerId") Long playerId) {
        List<Ranking> rankingsOfPlayer = rankingService.getRankingByPlayerId(playerId);
        return ResponseEntity.ok().body(rankingsOfPlayer);
    }

    @GetMapping("{leagueId}/{playerId}")
    public ResponseEntity<Ranking> getRankingByLeagueAndPlayer(@PathVariable("leagueId") Long leagueId, @PathVariable("playerId") Long playerId) {
        Ranking ranking = rankingService.getRankingByLeagueIdAndPlayerId(leagueId, playerId);
        return ResponseEntity.ok().body(ranking);
    }

}
