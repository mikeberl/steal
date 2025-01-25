package steal.app.backend.ranking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findRankingByLeagueIdAndPlayerId(long leagueId, long playerId);
    Optional<List<Ranking>> findRankingByLeagueId(long leagueId);
    Optional<List<Ranking>> findRankingByPlayerId(long playerId);
    void deleteRankingByLeagueIdAndPlayerId(Long leagueId, Long playerId);
    void deleteRankingByLeagueId(long leagueId);
    void deleteRankingByPlayerId(long playerId);

    @Query("SELECT new steal.app.backend.ranking.PlayerRankingDTO(r.id, r.totalScore, p.id, p.name, p.email, p.password) " +
            "FROM Ranking r JOIN Player p ON r.playerId = p.id " +
            "WHERE r.leagueId = :leagueId")
    List<PlayerRankingDTO> findPlayerRankingsByLeagueId(@Param("leagueId") Long leagueId);

}
