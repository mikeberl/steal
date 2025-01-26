package steal.app.backend.player;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @ElementCollection
    List<Long> leagues = new ArrayList<>();

    public Player() {}

    public Long getId() { return id;    }
    public void setId(Long id) { this.id = id;    }

    public String getName() { return name;    }
    public void setName(String name) { this.name = name;    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Long> getLeagues() { return leagues; }
    public void setLeagues(List<Long> leagues) { this.leagues = leagues; }
    public void addLeague(Long leagueId) { this.leagues.add(leagueId); }
    public void removeLeague(Long leagueId) { this.leagues.remove(leagueId); }
}
