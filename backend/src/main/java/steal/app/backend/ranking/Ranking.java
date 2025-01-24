package steal.app.backend.ranking;

import jakarta.persistence.*;

@Entity
@Table(name = "rankings")
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long leagueId;
    private Long playerId;

    private int totalScore = 0;

    public Ranking() {}

    public Long getLeagueId() { return leagueId; }
    public void setLeagueId(Long leagueId) { this.leagueId = leagueId; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalPoints) { this.totalScore = totalPoints; }
    public void updateTotalPoints(int score) { totalScore += score; }

}
