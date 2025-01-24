package steal.app.backend.match;

import jakarta.persistence.ElementCollection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MatchDTO {

    private Long id;

    private Long creatorId;
    private Long leagueId;
    private LocalDate date;

    private List<Long> winners;
    private List<Long> losers;

    private int winnerPoints;
    private int loserPoints;


    public MatchDTO() {}

    public MatchDTO(Match match) {
        this.id = match.getId();
        this.creatorId = match.getCreatorId();
        this.leagueId = match.getLeagueId();
        this.date = match.getDate();
        this.winners = match.getWinners();
        this.losers = match.getLosers();
        this.winnerPoints = match.getWinnerPoints();
        this.loserPoints = match.getLoserPoints();
    }

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

}
