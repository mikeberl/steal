package steal.app.backend.ranking;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rankings")
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long leagueId;
    private Long playerId;

    @ElementCollection
    private List<Long> matchIds;

    private int totalScore = 0;

    public Ranking() {}

    public Ranking(Long leagueId, Long playerId) {
        this.leagueId = leagueId;
        this.playerId = playerId;
        matchIds = new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeagueId() { return leagueId; }
    public void setLeagueId(Long leagueId) { this.leagueId = leagueId; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalPoints) { this.totalScore = totalPoints; }
    public void updateTotalScore(int score) { totalScore += score; }
    public void changeTotalScore(int prevScore, int newScore) { totalScore = totalScore - prevScore + newScore; }
    public void removeScore(int score) { totalScore -= score; }

    public List<Long> getMatchIds() { return matchIds;    }
    public void addMatchId(Long matchId) { this.matchIds.add(matchId); }
}
