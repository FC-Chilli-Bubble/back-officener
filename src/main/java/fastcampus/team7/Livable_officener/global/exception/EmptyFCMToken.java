package fastcampus.team7.Livable_officener.global.exception;

public class EmptyFCMToken extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "fcmToken이 비어있습니다.";

    public EmptyFCMToken() {
        this(DEFAULT_MESSAGE);
    }

    public EmptyFCMToken(String s) {
        super(s);
    }
}
