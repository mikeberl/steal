package steal.app.backend.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import steal.app.backend.league.LeagueRepository;
import steal.app.backend.match.Match;
import steal.app.backend.match.MatchDTO;
import steal.app.backend.match.MatchMapper;
import steal.app.backend.match.MatchRepository;
import steal.app.backend.player.PlayerRepository;
import steal.app.backend.ranking.Ranking;
import steal.app.backend.ranking.RankingRepository;

import java.util.ArrayList;
import java.util.List;


@Service
public class MatchScoreService {

    private static final Logger logger = LoggerFactory.getLogger(MatchScoreService.class);

    private final MatchRepository matchRepository;
    private final RankingRepository rankingRepository;
    private final LeagueRepository leagueRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public MatchScoreService(MatchRepository matchRepository, RankingRepository rankingRepository, LeagueRepository leagueRepository, PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.rankingRepository = rankingRepository;
        this.leagueRepository = leagueRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Match saveMatch(MatchDTO matchDTO) {
        if (this.leagueRepository.findById(matchDTO.getLeagueId()).isEmpty()) {
            throw new IllegalArgumentException("League does not exist");
        }
        if (!this.playerRepository.existsById(matchDTO.getCreatorId())) {
            throw new IllegalArgumentException("Creator does not exist");
        }
        List<Long> participants = new ArrayList<>(matchDTO.getWinners());
        participants.addAll(matchDTO.getLosers());
        if (participants.size() != this.playerRepository.findAllById(participants).size()) {
            throw new IllegalArgumentException("Some participants do not exist");
        }

        Match match = MatchMapper.toEntity(matchDTO);
        Match savedMatch = this.matchRepository.save(match);

        saveRankings(match, match.getWinners(), match.getWinnerPoints());
        saveRankings(match, match.getLosers(), match.getLoserPoints());

        return savedMatch;
    }

    public void saveRankings(Match match, List<Long> playerIds, int points) {
        playerIds.forEach(participant -> {
            Ranking ranking = rankingRepository.findRankingByLeagueIdAndPlayerId(match.getLeagueId(), participant).orElseThrow(
                    () -> new IllegalArgumentException("Ranking does not exist - Player with id " + participant + " is not registered in the league")
            );
            ranking.updateTotalScore(points);
            ranking.addMatchId(match.getId());
        });
    }

    @Transactional
    public Match updateMatch(MatchDTO matchDTO) {
        if (matchDTO.getId() == null) {
            throw new IllegalArgumentException("Match to update has no id");
        }
        Match existingMatch = matchRepository.findById(matchDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Match not found with id: " + matchDTO.getLeagueId()));

        modifyRankings(existingMatch, matchDTO.getWinners(), existingMatch.getWinnerPoints(), matchDTO.getWinnerPoints(), false);
        modifyRankings(existingMatch, matchDTO.getLosers(), existingMatch.getLoserPoints(), matchDTO.getLoserPoints(), false);

        existingMatch.setLeagueId(matchDTO.getLeagueId());
        existingMatch.setWinnerPoints(matchDTO.getWinnerPoints());
        existingMatch.setLoserPoints(matchDTO.getLoserPoints());
        existingMatch.setWinners(matchDTO.getWinners());
        existingMatch.setLosers(matchDTO.getLosers());

        return matchRepository.save(existingMatch);
    }

    private void modifyRankings(Match newMatch, List<Long> playerIds, int prevPoints, int newPoints, boolean deleteMatch) {
        playerIds.forEach(participant -> {
            Ranking ranking = rankingRepository.findRankingByLeagueIdAndPlayerId(newMatch.getLeagueId(), participant).orElseThrow(
                    () -> new EntityNotFoundException("Ranking of player " + participant + " not found")
            );
            ranking.changeTotalScore(prevPoints, newPoints);
            if (deleteMatch) {
                ranking.removeMatchId(newMatch.getId());
            }
            rankingRepository.save(ranking);
        });
    }

    @Transactional
    public void deleteMatch(Long matchId) {
        logger.info("DELETING Match with ID: {}", matchId);
        Match matchToDelete = matchRepository.findById(matchId).orElseThrow(
                () -> new RuntimeException("Match not found with id: " + matchId)
        );
        modifyRankings(matchToDelete, matchToDelete.getWinners(), matchToDelete.getWinnerPoints(), 0, true); // ranking is not removed just updated without the match
        modifyRankings(matchToDelete, matchToDelete.getLosers(), matchToDelete.getLoserPoints(), 0, true);
        matchRepository.delete(matchToDelete);
    }

}
