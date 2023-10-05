package fastcampus.team7.Livable_officener.dto.fcm;

import fastcampus.team7.Livable_officener.global.constant.FCMNotificationStatusUpdateType;
import lombok.Getter;

@Getter
public class FCMUpdateRequestDTO {

    private String email;
    private String fcmToken;
    private FCMNotificationStatusUpdateType type;

    public void setEmail(String email) {
        this.email = email;
    }
}
