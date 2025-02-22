package steal.app.backend.registrations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import steal.app.backend.player.PlayerRepository;
import steal.app.backend.ranking.Ranking;
import steal.app.backend.ranking.RankingRepository;

@RestController
@RequestMapping("api/v1/registrations")
public class RegistrationsController {

    private final RegistrationsService registrationsService;

    @Autowired
    public RegistrationsController(RegistrationsService registrationsService) {
        this.registrationsService = registrationsService;
    }


    @PostMapping("{leagueId}/{playerId}")
    public ResponseEntity<Ranking> registerPlayer(@PathVariable("leagueId") Long leagueId, @PathVariable("playerId") Long playerId) {
        Ranking ranking = this.registrationsService.registerPlayer(leagueId, playerId);
        return ResponseEntity.ok().body(ranking);
    }

    @DeleteMapping("{leagueId}/{playerId}")
    public ResponseEntity<Ranking> deregisterPlayer(@PathVariable("leagueId") Long leagueId, @PathVariable("playerId") Long playerId) {
        this.registrationsService.deRegisterPlayer(leagueId, playerId);
        return ResponseEntity.noContent().build();
    }

}
