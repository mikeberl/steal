package steal.app.backend.player;

import java.util.ArrayList;

public class PlayerMapper {

    public static PlayerDTO toDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setEmail(player.getEmail());
        dto.setPassword(player.getPassword());

        return dto;
    }

    public static Player toEntity(PlayerDTO dto) {
        Player player = new Player();
        player.setId(dto.getId());
        player.setName(dto.getName());
        player.setEmail(dto.getEmail());
        player.setPassword(dto.getPassword());
        player.setLeagues(new ArrayList<>());

        return player;
    }
}
