/**
 * Copyright 2016-2018 polaris9017 <moonrise917@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.projectn.vdl.core.event;

import com.google.common.base.Objects;
import kr.projectn.vdl.core.frame.SubmoduleFrame;

/**
 * Class for throwing submodule(s) event
 *
 * <h2>How to use</h2>
 * Pass its instances to {@link com.google.common.eventbus.EventBus#post(Object)}
 *
 * @since 1.0
 */
public class SubmoduleEvent {
    private SubmoduleFrame submodule;
    private String currentMethod;
    private Exception exception;
    private String errorMessage;

    /**
     * Creates a new SubmoduleEvent with submodule name and currently running method string
     *
     * @param submodule     submodule name
     * @param currentMethod currently running method string
     */
    public SubmoduleEvent(SubmoduleFrame submodule, String currentMethod) {
        this.submodule = submodule;
        this.currentMethod = currentMethod;
    }

    /**
     * Returns error message.<br><br>
     * Error message from exception instance will be returned if exception has been collected in advance, else
     * will return error message as user written.
     * @return error message
     */
    public String getErrorMessage() {
        if (Objects.equal(exception, null)) {
            return errorMessage;
        }

        return exception.getMessage();
    }

    /**
     * Returns exception instance
     * @return exception instance previously collected
     */
    public Exception getExceptionInstance() {
        return exception;
    }

    /**
     * Returns current submodule name
     * @return current submodule name string
     */
    public String getSubmodule() {
        return submodule.getClass().getSimpleName();
    }

    /**
     * Returns current running method string
     * @return current running method
     */
    public String getCurrentMethod() {
        return currentMethod;
    }

    /**
     * Returns currently working URL
     *
     * @return current URL
     */
    public String getUrl() {
        return submodule.getUrl();
    }

    /**
     * Collects exceptions if exception thrown
     * @param exception exception instance
     * @return {@code SubmoduleEvent} entity containing exception instance
     */
    public SubmoduleEvent setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    /**
     * Set user-custom error message
     * @param errorMessage error message string
     * @return {@code SubmoduleEvent} entity containing error message
     */
    public SubmoduleEvent setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
