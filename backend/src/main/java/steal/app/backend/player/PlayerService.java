package steal.app.backend.player;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player getPlayer(Long id) {
        return playerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Player with id " + id + " not found")
        );
    }

    public Player getPlayerByName(String name) {
        return playerRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Player with name " + name + " not found")
        );
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player createPlayer(Player player) {
        if (this.playerRepository.existsByName(player.getName())) {
            throw new EntityExistsException("Player with name " + player.getName() + " already exists");
        }
        return playerRepository.save(player);
    }

    public void deletePlayer(Long id) {
        if (!this.playerRepository.existsById(id)) {
            throw new EntityNotFoundException("Player with id " + id + " not found");
        }
        this.playerRepository.deleteById(id);
    }
}
