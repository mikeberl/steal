package steal.app.backend.match;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long creatorId;
    private Long leagueId;
    private LocalDate date;

    @ElementCollection
    private List<Long> winners;
    @ElementCollection
    private List<Long> losers;

    private int winnerPoints;
    private int loserPoints;

    private LocalDateTime lastModified;

    public Match() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCreatorId() {return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public Long getLeagueId() { return leagueId; }
    public void setLeagueId(Long leagueId) { this.leagueId = leagueId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<Long> getWinners() { return winners; }
    public void setWinners(List<Long> winners) { this.winners = winners; }

    public List<Long> getLosers() { return losers; }
    public void setLosers(List<Long> losers) { this.losers = losers; }

    public int getWinnerPoints() { return winnerPoints; }
    public void setWinnerPoints(int winnerPoints) { this.winnerPoints = winnerPoints; }

    public int getLoserPoints() { return loserPoints; }
    public void setLoserPoints(int loserPoints) { this.loserPoints = loserPoints;}

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
}
