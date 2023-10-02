package fastcampus.team7.Livable_officener.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import fastcampus.team7.Livable_officener.dto.chat.NotificationRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FCMService {

    public void send(final NotificationRequestDTO notificationRequestDTO) throws InterruptedException, ExecutionException {
        Message message = Message.builder()
                .setToken(notificationRequestDTO.getToken())
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
                        .setNotification(new WebpushNotification(notificationRequestDTO.getTitle(),
                                notificationRequestDTO.getMessage()))
                        .build())
                .build();

        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        log.info("Sent message: " + response);
    }

}
