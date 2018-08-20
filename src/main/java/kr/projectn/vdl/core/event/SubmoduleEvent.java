package kr.projectn.vdl.core.event;

public class SubmoduleEvent {
    private String submodule;
    private String currentMethod;
    private Exception exception;
    private String exceptionMessage;

    public SubmoduleEvent(String submodule, String currentMethod) {
        this.submodule = submodule;
        this.currentMethod = currentMethod;
    }

    public String getExceptionMessage() {
        return exception.getMessage();
    }

    public Exception getExceptionInstance() {
        return exception;
    }

    public String getSubmodule() {
        return submodule;
    }

    public String getCurrentMethod() {
        return currentMethod;
    }

    public SubmoduleEvent setException(Exception exception) {
        this.exception = exception;
        return this;
    }
}
