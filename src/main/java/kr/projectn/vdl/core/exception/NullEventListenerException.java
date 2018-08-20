package kr.projectn.vdl.core.exception;

public class NullEventListenerException extends NullPointerException {
    public NullEventListenerException() {
        super();
    }

    @Override
    public String getMessage() {
        return "";
    }
}
