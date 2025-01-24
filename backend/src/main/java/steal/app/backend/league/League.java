package steal.app.backend.league;


import jakarta.persistence.*;

@Entity
@Table(name="league")
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;
    private String name;
    private String sport;
    private String location;

    public League() {}

    public League(LeagueDTO leagueDto) {
        this.ownerId = leagueDto.getOwnerId();
        this.name = leagueDto.getName();
        this.sport = leagueDto.getSport();
        this.location = leagueDto.getLocation();
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
