package steal.app.backend.player;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/players")
public class PlayerController {

    private final PlayerService playerService;
    @Autowired
    public PlayerController(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("{playerId}")
    public ResponseEntity<Player> getPlayer(@PathVariable("playerId") Long playerId) {
        Player player = this.playerService.getPlayer(playerId);
        return ResponseEntity.ok().body(player);
    }

    @GetMapping("byName")
    public ResponseEntity<Player> getPlayerByNameQuery(@RequestParam("playerName") String playerName) {
        Player player = this.playerService.getPlayerByName(playerName);
        return ResponseEntity.ok().body(player);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        Player player = this.playerService.createPlayer(PlayerMapper.toEntity(playerDTO));
        player.setPassword(playerDTO.getPassword());
        return ResponseEntity.ok().body(player);
    }

    @DeleteMapping("{playerId}")
    public ResponseEntity<Player> deletePlayer(@PathVariable("playerId") Long playerId) {
        this.playerService.deletePlayer(playerId);
        return ResponseEntity.noContent().build();
    }

}
