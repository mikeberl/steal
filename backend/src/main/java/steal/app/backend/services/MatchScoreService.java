package steal.app.backend.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import steal.app.backend.match.Match;
import steal.app.backend.match.MatchDTO;
import steal.app.backend.match.MatchRepository;
import steal.app.backend.ranking.Ranking;
import steal.app.backend.ranking.RankingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class MatchScoreService {

    private final MatchRepository matchRepository;
    private final RankingRepository rankingRepository;

    @Autowired
    public MatchScoreService(MatchRepository matchRepository, RankingRepository rankingRepository) {
        this.matchRepository = matchRepository;
        this.rankingRepository = rankingRepository;
    }

    @Transactional
    public Match saveMatch(MatchDTO matchDTO) {
        Match match = new Match(matchDTO);

        saveRankings(matchDTO, matchDTO.getWinners(), matchDTO.getWinnerPoints());
        saveRankings(matchDTO, matchDTO.getLosers(), matchDTO.getLoserPoints());

        return matchRepository.save(match);
    }

    public void saveRankings(MatchDTO matchDTO, List<Long> playerIds, int points) {
        playerIds.forEach(participant -> {
            Optional<Ranking> ranking = rankingRepository.findRankingByLeagueIdAndPlayerId(matchDTO.getLeagueId(), participant);
            if (ranking.isPresent()) {
                ranking.get().updateTotalScore(points);
            } else {
                Ranking newRanking = new Ranking();
                newRanking.setLeagueId(matchDTO.getLeagueId());
                newRanking.setPlayerId(participant);
                newRanking.setTotalScore(points);
                newRanking.addMatchId(matchDTO.getId());
                rankingRepository.save(newRanking);
            }
        });
    }

    @Transactional
    public Match modifyMatch(Long matchId, MatchDTO matchDTO) {
        Match existingMatch = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));
        existingMatch.setLeagueId(matchDTO.getLeagueId());
        existingMatch.setWinnerPoints(matchDTO.getWinnerPoints());
        existingMatch.setLoserPoints(matchDTO.getLoserPoints());
        existingMatch.setWinners(matchDTO.getWinners());
        existingMatch.setLosers(matchDTO.getLosers());

        modifyRankings(matchDTO, matchDTO.getWinners(), existingMatch.getWinnerPoints(), matchDTO.getWinnerPoints());
        modifyRankings(matchDTO, matchDTO.getLosers(), existingMatch.getLoserPoints(), matchDTO.getLoserPoints());

        return matchRepository.save(existingMatch);
    }

    private void modifyRankings(MatchDTO newMatchDTO, List<Long> playerIds, int prevPoints, int newPoints) {
        playerIds.forEach(participant -> {
            Ranking ranking = rankingRepository.findRankingByLeagueIdAndPlayerId(newMatchDTO.getLeagueId(), participant).orElseThrow();
            ranking.changeTotalScore(prevPoints, newPoints);
            rankingRepository.save(ranking);
        });
    }

    private void deleteMatch(MatchDTO matchDTO) {
        matchRepository.deleteById(matchDTO.getId());
        modifyRankings(matchDTO, matchDTO.getWinners(), matchDTO.getWinnerPoints(), 0); // ranking is not removed just updated without the match
        modifyRankings(matchDTO, matchDTO.getLosers(), matchDTO.getLoserPoints(), 0);

    }

}
