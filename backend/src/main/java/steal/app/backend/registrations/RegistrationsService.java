package steal.app.backend.registrations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import steal.app.backend.league.LeagueRepository;
import steal.app.backend.player.Player;
import steal.app.backend.player.PlayerRepository;
import steal.app.backend.ranking.Ranking;
import steal.app.backend.ranking.RankingRepository;

@Service
public class RegistrationsService {

    private final PlayerRepository playerRepository;
    private final RankingRepository rankingRepository;
    private final LeagueRepository leagueRepository;

    @Autowired
    public RegistrationsService(PlayerRepository playerRepository, RankingRepository rankingRepository, LeagueRepository leagueRepository) {
        this.playerRepository = playerRepository;
        this.rankingRepository = rankingRepository;
        this.leagueRepository = leagueRepository;
    }

    public Ranking registerPlayer(Long leagueId, Long playerId) {
        if (!leagueRepository.existsById(leagueId)) {
            throw new IllegalArgumentException("League does not exist");
        }
        if (!playerRepository.existsById(playerId)) {
            throw new IllegalArgumentException("Player does not exist");
        }
        if (rankingRepository.findRankingByLeagueIdAndPlayerId(leagueId, playerId).isPresent()) {
            throw new IllegalArgumentException("Ranking already exists");
        }
        Player player = playerRepository.findById(playerId).orElseThrow();
        player.addLeague(leagueId);
        return rankingRepository.save(new Ranking(leagueId, playerId));

    }
}
