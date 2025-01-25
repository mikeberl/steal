package steal.app.backend.ranking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findRankingByLeagueIdAndPlayerId(long leagueId, long playerId);
    Optional<List<Ranking>> findRankingByLeagueId(long leagueId);
    Optional<List<Ranking>> findRankingByPlayerId(long playerId);
    void deleteRankingByLeagueIdAndPlayerId(Long leagueId, Long playerId);
    void deleteRankingByLeagueId(long leagueId);
    void deleteRankingByPlayerId(long playerId);

}
