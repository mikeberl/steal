package steal.app.backend.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findMatchesByLeagueId(Long leagueId);

}
