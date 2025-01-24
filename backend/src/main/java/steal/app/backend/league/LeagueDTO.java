package steal.app.backend.league;

import jakarta.validation.constraints.NotNull;


public class LeagueDTO {

    private Long id;
    @NotNull(message = "Text cannot be null")
    private Long ownerId;
    private String name;
    private String sport;
    private String location;

    public LeagueDTO() {}

    public LeagueDTO(League league) {
        this.id = league.getId();
        this.ownerId = league.getOwnerId();
        this.name = league.getName();
        this.sport = league.getSport();
        this.location = league.getLocation();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
