package steal.app.backend.match;

import java.time.LocalDateTime;

public class MatchMapper {

    public static MatchDTO toDTO(Match match) {
        MatchDTO dto = new MatchDTO();
        dto.setId(match.getId());
        dto.setCreatorId(match.getCreatorId());
        dto.setLeagueId(match.getLeagueId());
        dto.setDate(match.getDate());

        dto.setWinners(match.getWinners());
        dto.setLosers(match.getLosers());
        dto.setWinnerPoints(match.getWinnerPoints());
        dto.setLoserPoints(match.getWinnerPoints());
        dto.setLastModified(match.getLastModified());
        return dto;
    }

    public static Match toEntity(MatchDTO dto) {
        Match match = new Match();
        match.setId(dto.getId());
        match.setCreatorId(dto.getCreatorId());
        match.setLeagueId(dto.getLeagueId());
        match.setDate(dto.getDate());

        match.setWinners(dto.getWinners());
        match.setLosers(dto.getLosers());
        match.setWinnerPoints(dto.getWinnerPoints());
        match.setLoserPoints(dto.getLoserPoints());
        match.setLastModified(LocalDateTime.now());

        return match;
    }
}
