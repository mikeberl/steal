package steal.app.backend.ranking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {
    private RankingRepository rankingRepository;

    @Autowired
    public RankingService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    public List<Ranking> getRankings() {
        return rankingRepository.findAll();
    }

    public Ranking getRankingById(Long id) {
        return rankingRepository.findById(id).orElseThrow();
    }

    public List<Ranking> getRankingByPlayerId(Long playerId) {
        return rankingRepository.findRankingByPlayerId(playerId).orElseThrow();
    }

    public List<Ranking> getRankingByLeagueId(Long leagueId) {
        return rankingRepository.findRankingByLeagueId(leagueId).orElseThrow();
    }

    public Ranking getRankingByLeagueIdAndPlayerId(Long leagueId, Long playerId) {
        return rankingRepository.findRankingByLeagueIdAndPlayerId(leagueId, playerId).orElseThrow();
    }

    public List<PlayerRankingDTO> getPlayerRankingsForLeague(Long leagueId) {
        return rankingRepository.findPlayerRankingsByLeagueId(leagueId);
    }
}
