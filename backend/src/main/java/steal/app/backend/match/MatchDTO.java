package steal.app.backend.match;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MatchDTO {

    private Long id;

    @NotNull(message = "creatorId cannot be null")
    private Long creatorId;
    @NotNull(message = "leagueId cannot be null")
    private Long leagueId;
    private LocalDate date;

    @NotEmpty(message = "Match cannot have no winners")
    private List<Long> winners;
    @NotEmpty(message = "Match cannot have no losers")
    private List<Long> losers;

    @NotNull(message = "Match must have winnerPoints")
    private int winnerPoints;
    @NotNull(message = "Match must have loserPoints")
    private int loserPoints;

    private LocalDateTime lastModified;


    public MatchDTO() {}

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
