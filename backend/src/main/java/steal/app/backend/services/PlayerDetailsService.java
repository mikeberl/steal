package steal.app.backend.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import steal.app.backend.player.Player;
import steal.app.backend.player.PlayerRepository;

@Service
public class PlayerDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    public PlayerDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Player player = playerRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("Player not found with name: " + name));
        return new PlayerDetails(player);
    }
}

