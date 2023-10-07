package fastcampus.team7.Livable_officener.global.exception;

public class EmptyFCMUpdateRequestType extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "type이 존재하지 않습니다.";

    public EmptyFCMUpdateRequestType() {
        this(DEFAULT_MESSAGE);
    }

    public EmptyFCMUpdateRequestType(String s) {
        super(s);
    }
}
