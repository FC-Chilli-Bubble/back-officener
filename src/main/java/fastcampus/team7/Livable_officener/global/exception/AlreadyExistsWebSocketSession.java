package fastcampus.team7.Livable_officener.global.exception;

public class AlreadyExistsWebSocketSession extends IllegalStateException {

    public static final String DEFAULT_MESSAGE = "웹소켓 세션은 채팅방마다 참여자별로 하나만 연결 가능합니다.";

    public AlreadyExistsWebSocketSession() {
        this(DEFAULT_MESSAGE);
    }

    public AlreadyExistsWebSocketSession(String s) {
        super(s);
    }
}
