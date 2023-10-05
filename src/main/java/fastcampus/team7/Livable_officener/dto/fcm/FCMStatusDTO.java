package fastcampus.team7.Livable_officener.dto.fcm;

import fastcampus.team7.Livable_officener.global.constant.FCMNotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FCMStatusDTO {
    private String fcmToken;
    private FCMNotificationStatus status;
}
