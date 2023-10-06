package fastcampus.team7.Livable_officener.dto.delivery;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class RoomListResponseDTO {
    private Long roomId;
    private Long hostId;
    private String storeName;
    private String menuLink;
    private Long deliveryFee;
    private String tag;
    private Long attendees;
    private Long maxAttendees;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadLine;
    private String roomStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
