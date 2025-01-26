package steal.app.backend.player;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PlayerDTO {

    private Long id;
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    private List<Long> leagues;

    public PlayerDTO() {}

    public Long getId() { return id;    }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name;    }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Long> getLeagues() { return leagues; }
    public void setLeagues(List<Long> leagues) { this.leagues = leagues; }
    public void addLeague(Long leagueId) { this.leagues.add(leagueId); }
    public void removeLeague(Long leagueId) { this.leagues.remove(leagueId); }
}
