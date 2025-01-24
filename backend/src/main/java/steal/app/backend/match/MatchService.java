package steal.app.backend.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(final MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Match findMatchById(Long id) {
        return matchRepository.findById(id).orElseThrow();
    }

    public List<Match> getByLeague(Long leagueId) {

        List<Match> matches = matchRepository.findMatchesByLeagueId(leagueId);
        if (matches.isEmpty()) {
            throw new RuntimeException("League with id " + leagueId + " has no matches");
        }

        return matches;
    }
}
