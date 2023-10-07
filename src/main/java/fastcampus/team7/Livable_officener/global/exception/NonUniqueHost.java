package fastcampus.team7.Livable_officener.global.exception;

public class NonUniqueHost extends IllegalStateException {

    public static final String DEFAULT_MESSAGE = "해당 함께배달에 호스트가 존재하지 않거나 둘 이상 존재합니다.";

    public NonUniqueHost() {
        this(DEFAULT_MESSAGE);
    }

    public NonUniqueHost(String s) {
        super(s);
    }
}
