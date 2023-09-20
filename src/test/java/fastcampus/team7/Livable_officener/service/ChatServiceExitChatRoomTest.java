package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.exception.*;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChatServiceExitChatRoomTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private DeliveryRepository roomRepository;
    @Mock
    private DeliveryParticipantRepository roomParticipantRepository;

    @Test
    @DisplayName("나가기 시 채팅방 Id 없으면 예외 발생")
    void roomIdNotFoundTest() {
        //given
        Long WrongRoomId = 1L;
        User user = mock(User.class);

        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when//then
        customAssertThrow(WrongRoomId, user, NotFoundRoomException.class);
    }

    @Test
    @DisplayName("나가기 시 해당 채팅방의 참여자가 아니면 예외발생")
    void isParticipantTest() {
        // given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);

        given(user.getId())
                .willReturn(1L);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(roomParticipantRepository.findRoomParticipant(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        // when, then
        customAssertThrow(roomId, user, UserIsNotParticipantException.class);
    }

    @Test
    @DisplayName("나가기 시 호스트가 아니면 예외발생")
    void isHostTest() {
        // given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);
        RoomParticipant roomParticipant = mock(RoomParticipant.class);

        given(user.getId())
                .willReturn(1L);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(roomParticipantRepository.findRoomParticipant(anyLong(), anyLong()))
                .willReturn(Optional.of(roomParticipant));
        given(roomParticipant.getRole())
                .willReturn(Role.GUEST);

        // when, then
        customAssertThrow(roomId, user, UserIsNotHostException.class);
    }

    void customAssertThrow(Long roomId, User user,
                           Class<? extends Throwable> ex) {
        assertThatThrownBy(() ->
                sut.exitChatRoom(roomId, user))
                .isInstanceOf(ex);
    }
}