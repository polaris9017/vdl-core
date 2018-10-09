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
package kr.projectn.vdl.core;

import com.google.common.base.Objects;
import kr.projectn.vdl.core.event.DefaultSubmoduleEventListener;
import kr.projectn.vdl.core.event.SubmoduleEventListener;
import kr.projectn.vdl.core.frame.ServiceType;
import kr.projectn.vdl.core.frame.SubmoduleCode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Builds {@link Response} instance
 *
 * @since 1.0
 */
public class RequestBuilder {

    private Queue<String> urlList;
    private Queue<SubmoduleCode> submoduleCodeList;
    private int start;  //start point at vlive_ch
    private int end;  //end point at vlive_ch
    private SubmoduleEventListener listener; //event listener

    /**
     * Creates a new {@code RequestBuilder} instance
     */
    public RequestBuilder() {
        urlList = new LinkedList<>();
        submoduleCodeList = new LinkedList<>();
    }

    /**
     * Add URL to internal list
     * @param url URL string
     * @return {@code RequestBuilder} entity contains URL
     */
    public RequestBuilder setUrl(String url) {
        urlList.offer(url);

        return this;
    }

    /**
     * Build a new {@link Request} instance using URL and submodule identification code
     * @return new {@link Request} instance
     */
    public Request build() {
        this.setSubmoduleCode();

        if (Objects.equal(listener, null))
            listener = new DefaultSubmoduleEventListener();

        return new Request(urlList, submoduleCodeList, listener);
    }

    /**
     * Build a new {@link Request} instance using URL and submodule identification code with start, end index of list
     * @param start start index
     * @param end end index (must be less to length of list)
     * @return new {@link Request} instance with list index
     */
    public Request build(int start, int end) {
        this.setSubmoduleCode();

        if (Objects.equal(listener, null))
            listener = new DefaultSubmoduleEventListener();

        return new Request(urlList, submoduleCodeList, listener, start, end);
    }

    private void setSubmoduleCode() {
        for (String url : urlList) {
            submoduleCodeList.offer(SubmoduleCode.findSubModuleByType(
                    ServiceType.findServiceByURL(url)
            ));
        }
    }

    /**
     * Set a {@link com.google.common.eventbus.EventBus} event listener to submodule<br><br>
     * Every listener should implement {@link SubmoduleEventListener} interface.
     * @param listener listener instance
     * @return {@code RequestBuilder} entity contains event listener
     */
    public RequestBuilder setListener(SubmoduleEventListener listener) {
        this.listener = listener;
        return this;
    }
}
