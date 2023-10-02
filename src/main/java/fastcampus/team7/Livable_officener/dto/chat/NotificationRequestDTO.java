package fastcampus.team7.Livable_officener.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationRequestDTO {

    private String title;
    private String message;
    private String token;

    @Builder
    public NotificationRequestDTO(String title, String message, String token) {
        this.title = title;
        this.message = message;
        this.token = token;
    }

}