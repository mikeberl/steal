package steal.app.backend.league;

public class LeagueMapper {

    public static LeagueDTO toDTO(League league) {
        LeagueDTO dto = new LeagueDTO();
        dto.setId(league.getId());
        dto.setOwnerId(league.getOwnerId());
        dto.setName(league.getName());
        dto.setLocation(league.getLocation());
        dto.setSport(league.getSport());
        return dto;
    }

    public static League toEntity(LeagueDTO dto) {
        League league = new League();
        league.setId(dto.getId());
        league.setOwnerId(dto.getOwnerId());
        league.setName(dto.getName());
        league.setLocation(dto.getLocation());
        league.setSport(dto.getSport());
        return league;
    }
}
