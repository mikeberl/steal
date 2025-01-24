package steal.app.backend.score;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    public Optional<Score> findByMatchIdAndPlayerId(Long matchId, Long playerId);

    public void deleteByMatchId(Long matchId);
}
