package steal.app.backend.ranking;

public class PlayerRankingDTO {
    private Long rankingId;
    private Integer score;
    private Long playerId;
    private String name;
    private String email;
    private String password;

    public PlayerRankingDTO() {}

    public PlayerRankingDTO(Long rankingId, Integer score, Long playerId, String name, String email, String password) {
        this.rankingId = rankingId;
        this.score = score;
        this.playerId = playerId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
