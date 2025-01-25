package steal.app.backend.player;

public class PlayerMapper {

    public static PlayerDTO toDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setEmail(player.getEmail());
        dto.setPassword(player.getPassword());
        dto.setLeagues(player.getLeagues());

        return dto;
    }

    public static Player toEntity(PlayerDTO dto) {
        Player player = new Player();
        player.setId(dto.getId());
        player.setName(dto.getName());
        player.setEmail(dto.getEmail());
        player.setPassword(dto.getPassword());
        player.setLeagues(dto.getLeagues());

        return player;
    }
}
