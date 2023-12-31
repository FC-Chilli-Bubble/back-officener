package fastcampus.team7.Livable_officener.dto.fcm;

import fastcampus.team7.Livable_officener.global.constant.FCMNotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FCMStatusDTO {
    private String fcmToken;
    private FCMNotificationStatus status;

    @Override
    public String toString() {
        return "FCMStatusDTO{" +
                "fcmToken='" + fcmToken + '\'' +
                ", status=" + status +
                '}';
    }
}
