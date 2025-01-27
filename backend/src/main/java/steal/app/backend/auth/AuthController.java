package steal.app.backend.auth;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import steal.app.backend.player.Player;
import steal.app.backend.player.PlayerRepository;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final PlayerRepository playerRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, PlayerRepository playerRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public String nothing() {
        return "Success";
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        Player player = playerRepository.findByName(authRequest.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Player not found"));
        return jwtService.generateToken(player.getName());
    }
}

class AuthRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

