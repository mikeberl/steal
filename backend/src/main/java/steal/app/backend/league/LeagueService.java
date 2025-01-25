package steal.app.backend.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import steal.app.backend.player.PlayerRepository;

import java.util.List;

@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public LeagueService(LeagueRepository leagueRepository, PlayerRepository playerRepository) {
        this.leagueRepository = leagueRepository;
        this.playerRepository = playerRepository;
    }

    public List<League> getAllLeagues() {
            return leagueRepository.findAll();
    }

    public League getLeagueById(Long id) {
        return leagueRepository.findById(id).orElseThrow();
    }

    public League addLeague(League league) {
        if (!playerRepository.existsById(league.getOwnerId())){
            throw new RuntimeException("Owner does not exist");
        }
        if (leagueRepository.findByName(league.getName()) != null){
            throw new RuntimeException("League already exists");
        }
        return leagueRepository.save(league);
    }

    public League updateLeague(League league) {
        if (!leagueRepository.existsById(league.getId())) {
            throw new RuntimeException("League does not exist");
        }
        return leagueRepository.save(league);
    }

    public void deleteLeague(Long id) {
        leagueRepository.deleteById(id);
    }
}
