package fastcampus.team7.Livable_officener.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import fastcampus.team7.Livable_officener.dto.fcm.FCMNotificationDTO;
import fastcampus.team7.Livable_officener.dto.fcm.FCMStatusDTO;
import fastcampus.team7.Livable_officener.dto.fcm.FCMUpdateRequestDTO;
import fastcampus.team7.Livable_officener.global.constant.FCMNotificationStatus;
import fastcampus.team7.Livable_officener.global.constant.FCMNotificationStatusUpdateType;
import fastcampus.team7.Livable_officener.global.exception.EmptyFCMUpdateRequestType;
import fastcampus.team7.Livable_officener.global.exception.EmptyFCMToken;
import fastcampus.team7.Livable_officener.global.exception.NotFoundFCMToken;
import fastcampus.team7.Livable_officener.global.fcm.FCMStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService {

    private final FCMStatusRepository fcmStatusRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    public void update(FCMUpdateRequestDTO dto) {
        FCMNotificationStatusUpdateType type = dto.getType();
        String fcmToken = dto.getFcmToken();
        if (type == FCMNotificationStatusUpdateType.ACTIVATE) {
            validateIfTokenHasText(fcmToken);
            log.info("알림 켜기 - {}", dto.getEmail());
            turnOnNotificationPushing(dto);
        } else if (type == FCMNotificationStatusUpdateType.DEACTIVATE) {
            log.info("알림 끄기 - {}", dto.getEmail());
            turnOffNotificationPushing(dto);
        } else if (type == FCMNotificationStatusUpdateType.KEEP) {
            // 로그인
            validateIfTokenHasText(fcmToken);
            String email = dto.getEmail();
            if (fcmStatusRepository.contains(email)) {
                // 존재하면 가져와서 token만 갱신
                FCMStatusDTO fcmStatusDTO = fcmStatusRepository.get(email);
                fcmStatusDTO.setFcmToken(fcmToken);
                log.info("로그인 (알림 상태 유지:{}) - {}", dto.getType(), dto.getEmail());
                fcmStatusRepository.save(email, fcmStatusDTO);
            } else {
                // 없으면 알림 켜기로 갱신
                log.info("로그인 (알림 켜기) - {}", dto.getEmail());
                turnOnNotificationPushing(dto);
            }
        } else {
            throw new EmptyFCMUpdateRequestType();
        }
    }

    private static void validateIfTokenHasText(String fcmToken) {
        if (!StringUtils.hasText(fcmToken)) {
            throw new EmptyFCMToken();
        }
    }

    private void turnOnNotificationPushing(FCMUpdateRequestDTO dto) {
        FCMStatusDTO fcmStatusDTO = new FCMStatusDTO(dto.getFcmToken(), FCMNotificationStatus.ACTIVE);
        fcmStatusRepository.save(dto.getEmail(), fcmStatusDTO);
    }

    private void turnOffNotificationPushing(FCMUpdateRequestDTO dto) {
        FCMStatusDTO fcmStatusDTO = new FCMStatusDTO(null, FCMNotificationStatus.INACTIVE);
        fcmStatusRepository.save(dto.getEmail(), fcmStatusDTO);
    }

    @Transactional
    public void sendFcmNotification(FCMNotificationDTO dto) {
        String fcmToken = getFcmToken(dto.getReceiverEmail());

        Notification notification = dto.makeNotification();
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(fcmToken)
                .build();

        try {
            String messageId = firebaseMessaging.send(message);
            log.info("웹푸시 전송 to {}: {}", dto.getReceiverEmail(), messageId);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("웹푸시 전송 실패", e);
        }
    }

    private String getFcmToken(String email) {
        FCMStatusDTO dto = fcmStatusRepository.get(email);
        String fcmToken = dto.getFcmToken();
        if (fcmToken == null) {
            throw new NotFoundFCMToken();
        }
        return fcmToken;
    }

    @Transactional
    public void deleteToken(String email) {
        if (fcmStatusRepository.contains(email)) {
            FCMStatusDTO dto = fcmStatusRepository.get(email);
            dto.setFcmToken(null);
            log.info("로그아웃 (알림 상태 유지:{}) - {}", dto.getStatus(), email);
            fcmStatusRepository.save(email, dto);
        }
    }

    @Transactional(readOnly = true)
    public boolean isSubscribed(String email) {
        FCMStatusDTO dto = fcmStatusRepository.get(email);
        if (!StringUtils.hasText(dto.getFcmToken())) {
            return false;
        }
        return dto.getStatus() == FCMNotificationStatus.ACTIVE;
    }
}
