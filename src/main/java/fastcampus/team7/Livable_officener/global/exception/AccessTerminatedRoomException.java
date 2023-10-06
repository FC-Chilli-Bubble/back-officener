package fastcampus.team7.Livable_officener.global.exception;

public class AccessTerminatedRoomException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "해당 ID의 함께배달 방은 종료되어 접근할 수 없습니다.";

    public AccessTerminatedRoomException() {
        this(DEFAULT_MESSAGE);
    }

    public AccessTerminatedRoomException(String s) {
        super(s);
    }
}
