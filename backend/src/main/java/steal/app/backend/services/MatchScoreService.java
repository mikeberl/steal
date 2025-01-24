package steal.app.backend.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import steal.app.backend.match.Match;
import steal.app.backend.match.MatchDTO;
import steal.app.backend.match.MatchRepository;
import steal.app.backend.score.Score;
import steal.app.backend.score.ScoreRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class MatchScoreService {

    private final MatchRepository matchRepository;
    private final ScoreRepository scoreRepository;

    @Autowired
    public MatchScoreService(MatchRepository matchRepository, ScoreRepository scoreRepository) {
        this.matchRepository = matchRepository;
        this.scoreRepository = scoreRepository;
    }

    public Match saveMatch(MatchDTO matchDTO) {
        Match match = new Match(matchDTO);

        matchDTO.getWinners().forEach(winner -> {
            Score score = new Score();
            score.setPlayerId(winner);
            score.setMatchId(match.getId());
            score.setLeagueId(match.getLeagueId());
            score.setScore(match.getWinnerPoints());
            score.setLastModified(LocalDateTime.now());
        });

        matchDTO.getLosers().forEach(winner -> {
            Score score = new Score();
            score.setPlayerId(winner);
            score.setMatchId(match.getId());
            score.setLeagueId(match.getLeagueId());
            score.setScore(match.getLoserPoints());
            score.setLastModified(LocalDateTime.now());
        });
        return matchRepository.save(match);
    }

    @Transactional
    public Match modifyMatch(Long matchId, MatchDTO matchDTO) {
        Match existingMatch = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        existingMatch.setLeagueId(matchDTO.getLeagueId());
        existingMatch.setWinnerPoints(matchDTO.getWinnerPoints());
        existingMatch.setLoserPoints(matchDTO.getLoserPoints());
        existingMatch.setWinners(matchDTO.getWinners());
        existingMatch.setLosers(matchDTO.getLosers());

        updateScores(matchId, matchDTO.getWinners(), matchDTO.getWinnerPoints());
        updateScores(matchId, matchDTO.getLosers(), matchDTO.getLoserPoints());

        return matchRepository.save(existingMatch);
    }

    private void updateScores(Long matchId, List<Long> playerIds, int points) {
        for (Long playerId : playerIds) {
            Optional<Score> optionalScore = scoreRepository.findByMatchIdAndPlayerId(matchId, playerId);

            Score score = optionalScore.orElseThrow(() ->
                    new IllegalArgumentException("Score not found for match id: " + matchId + " and player id: " + playerId)
            );
            score.setScore(points);
            score.setLastModified(LocalDateTime.now());
            scoreRepository.save(score);
        }
    }
    private void deleteScores(Long matchId) {
        matchRepository.deleteById(matchId);
        scoreRepository.deleteByMatchId(matchId);
    }
}
