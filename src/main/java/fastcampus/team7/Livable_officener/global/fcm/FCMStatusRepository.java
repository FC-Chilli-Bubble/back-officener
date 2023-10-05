package fastcampus.team7.Livable_officener.global.fcm;

import fastcampus.team7.Livable_officener.dto.fcm.FCMStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FCMStatusRepository {

    private final RedisTemplate<String, FCMStatusDTO> fcmStatusRedisTemplate;

    public void save(String email, FCMStatusDTO dto) {
        log.info("FCM 토큰 저장 - {} - {}", email, dto);
        fcmStatusRedisTemplate.opsForValue()
                .set(email, dto);
    }

    public boolean contains(String email) {
        return Boolean.TRUE.equals(fcmStatusRedisTemplate.hasKey(email));
    }

    public FCMStatusDTO get(String email) {
        FCMStatusDTO fcmStatusDTO = fcmStatusRedisTemplate.opsForValue().get(email);
        if (fcmStatusDTO == null) {
            throw new IllegalCallerException(email + "님의 FCM 상태가 Redis에 존재하지 않습니다.");
        }
        return fcmStatusDTO;
    }
}
