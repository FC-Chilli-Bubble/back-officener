package fastcampus.team7.Livable_officener.global.exception;

import fastcampus.team7.Livable_officener.global.constant.FilterExceptionCode;

public class AlreadyLogoutException extends IllegalStateException {
    public AlreadyLogoutException(FilterExceptionCode filterExceptionCode) {
        super(filterExceptionCode.getMessage());
    }
}
