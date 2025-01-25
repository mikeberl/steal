package steal.app.backend.league;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository  extends JpaRepository<League, Long> {
    League findByName(String name);
}

