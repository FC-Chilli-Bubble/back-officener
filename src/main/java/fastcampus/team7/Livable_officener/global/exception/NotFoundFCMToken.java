package fastcampus.team7.Livable_officener.global.exception;

public class NotFoundFCMToken extends IllegalStateException {

    public static final String DEFAULT_MESSAGE = "fcmToken이 존재하지 않습니다.";

    public NotFoundFCMToken() {
        this(DEFAULT_MESSAGE);
    }

    public NotFoundFCMToken(String s) {
        super(s);
    }
}
