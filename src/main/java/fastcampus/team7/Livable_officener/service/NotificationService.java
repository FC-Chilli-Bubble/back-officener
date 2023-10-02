package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Notification;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.NotificationDTO;
import fastcampus.team7.Livable_officener.dto.chat.NotificationRequestDTO;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.NotificationRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final FCMService fcmService;
    private final Map<Long, String> tokenMap = new HashMap<>();

    public ResponseEntity<APIDataResponse<List<NotificationDTO>>> getNotifyList(String token){
        String email = jwtProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당하는 유저가 없습니다."));;
        Long id = user.getId();
        List<Notification> notifications = notificationRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("해당하는 유저에 알림이 없습니다."));

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for(Notification notification : notifications){
            notificationDTOS.add(toDTO(notification));
        }

        ResponseEntity<APIDataResponse<List<NotificationDTO>>> responseEntity = APIDataResponse.of(
                HttpStatus.OK, notificationDTOS);

        return responseEntity;
    }

    public ResponseEntity<APIDataResponse<String>> readAll(String token){
        String email = jwtProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당하는 유저가 없습니다."));;
        Long id = user.getId();

        List<Notification> notifications = notificationRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("해당하는 유저에 알림이 없습니다."));

        for(Notification notification : notifications){
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        ResponseEntity<APIDataResponse<String>> responseEntity = APIDataResponse.empty(
                HttpStatus.OK);

        return responseEntity;
    }

    public ResponseEntity<APIDataResponse<String>> readNotify(String token,Long notifyId){
            String email = jwtProvider.getEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당하는 유저가 없습니다."));
            ;
            Long id = user.getId();

            Notification notification = notificationRepository.findById(notifyId)
                    .orElseThrow(() -> new RuntimeException("해당하는 유저에 알림이 없습니다."));

            if (notification.getUser().getId() == id) {
                notification.setRead(true);
                notificationRepository.save(notification);
            } else {
                throw new RuntimeException("토큰 정보와 알림이 일치하지 않습니다.");
            }
        ResponseEntity<APIDataResponse<String>> responseEntity = APIDataResponse.empty(
                HttpStatus.OK);

        return responseEntity;
    }

        public NotificationDTO toDTO (Notification entity){
            NotificationDTO DTO = new NotificationDTO();
            DTO.setId(entity.getId());
            DTO.setReceiverId(entity.getUser().getId());
            DTO.setRoomId(entity.getRoom().getId());
            DTO.setContent(entity.getNotificationContent().getName());
            DTO.setType(entity.getNotificationType().getName());
            DTO.setRead(entity.isRead());
            DTO.setMenuTag(entity.getFoodTag().toString());
            DTO.setCreatedAt(entity.getCreatedAt());
            return DTO;
        }

    public void register(final Long userId, final String token) {
        tokenMap.put(userId, token);
    }

    public void deleteToken(final Long userId) {
        tokenMap.remove(userId);
    }

    public String getToken(final Long userId) {
        return tokenMap.get(userId);
    }

    public void sendNotification(final NotificationRequestDTO request) {
        try {
            fcmService.send(request);
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
    }

}
