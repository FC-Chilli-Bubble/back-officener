package fastcampus.team7.Livable_officener.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.GetParticipantDTO;
import fastcampus.team7.Livable_officener.dto.chat.SendPayloadDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class WebSocketSessionManager {

    private final Map<Long, Collection<WebSocketSession>> roomIdToSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public static User getSessionUser(WebSocketSession session) {
        return (User) session.getPrincipal();
    }

    public void addRoomInWebSocketSessionMap(Long roomId) {
        if (roomIdToSessions.containsKey(roomId)) {
            return;
        }
        List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
        roomIdToSessions.put(roomId, sessions);
    }

    public void removeRoomFromWebSocketSessionMap(Long roomId) {
        roomIdToSessions.remove(roomId);
    }

    public void addSessionToRoom(Long roomId, WebSocketSession session) {
        addRoomInWebSocketSessionMap(roomId);

        Collection<WebSocketSession> sessions = getWebSocketSessions(roomId);
        User requestUser = getSessionUser(session);
        Optional<WebSocketSession> duplicateUserSession = sessions.stream()
                .filter(sess -> getSessionUser(sess).equals(requestUser))
                .findFirst();
        if (duplicateUserSession.isPresent()) {
            throw new IllegalStateException("웹소켓 세션은 채팅방마다 참여자별로 하나만 연결 가능합니다.");
        }
        sessions.add(session);
    }

    public void closeSessionForUser(Long roomId, User kickedUser) {
        for (WebSocketSession session : getWebSocketSessions(roomId)) {
            if (kickedUser.equals(getSessionUser(session))) {
                removeSessionFromRoom(roomId, session);
                return;
            }
        }
    }

    public void removeSessionFromRoom(Long roomId, WebSocketSession session) {
        Collection<WebSocketSession> sessions = getWebSocketSessions(roomId);
        sessions.remove(session);
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToAll(Long roomId, TextMessage message) throws IOException {
        Collection<WebSocketSession> webSocketSessions = getWebSocketSessions(roomId);
        for (WebSocketSession webSocketSession : webSocketSessions) {
            webSocketSession.sendMessage(message);
        }
    }

    public boolean nonexistent(Long roomId, User user) {
        for (WebSocketSession session : getWebSocketSessions(roomId)) {
            if (user.equals(getSessionUser(session))) {
                return false;
            }
        }
        return true;
    }

    private Collection<WebSocketSession> getWebSocketSessions(Long roomId) {
        var ret = roomIdToSessions.get(roomId);
        if (ret == null) {
            throw new NotFoundRoomException();
        }
        return ret;
    }

    public void sendDynamicMessageToAll(Long roomId, SendPayloadDTO payloadDto) throws IOException {
        ChatType messageType = payloadDto.getMessageType();
        for (WebSocketSession session : getWebSocketSessions(roomId)) {
            User user = getSessionUser(session);
            String content = messageType.getSystemMessageContent(user);
            payloadDto.setContent(content);
            String payload = objectMapper.writeValueAsString(payloadDto);
            TextMessage message = new TextMessage(payload);
            session.sendMessage(message);
        }
    }

    public void sendEnterMessageToAll(
            Long roomId,
            User enteringUser,
            SendPayloadDTO.Enter enterPayloadDto) throws IOException {

        GetParticipantDTO newParticipantDto = enterPayloadDto.getNewParticipant();
        Collection<WebSocketSession> sessions = getWebSocketSessions(roomId);
        for (WebSocketSession session : sessions) {
            User receiver = getSessionUser(session);
            newParticipantDto.setAmI(enteringUser.equals(receiver));
            String payload = objectMapper.writeValueAsString(enterPayloadDto);
            TextMessage message = new TextMessage(payload);
            session.sendMessage(message);
        }
    }
}
