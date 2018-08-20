package kr.projectn.vdl.core.event;

import com.google.common.base.Objects;

public class SubmoduleEvent {
    private String submodule;
    private String currentMethod;
    private Exception exception;
    private String errorMessage;

    public SubmoduleEvent(String submodule, String currentMethod) {
        this.submodule = submodule;
        this.currentMethod = currentMethod;
    }

    public String getErrorMessage() {
        if (Objects.equal(exception, null)) {
            return errorMessage;
        }

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

    public SubmoduleEvent setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
