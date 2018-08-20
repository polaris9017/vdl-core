/**
 * Copyright 2016-2018 qscx9512 <moonrise917@gmail.com>
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

import kr.projectn.vdl.core.event.SubmoduleEventListener;
import kr.projectn.vdl.core.exception.NullEventListenerException;
import kr.projectn.vdl.core.frame.ServiceType;
import kr.projectn.vdl.core.frame.SubmoduleCode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The type Request builder.
 */
public class RequestBuilder {

    private Queue<String> urlList;
    private Queue<SubmoduleCode> submoduleCodeList;
    private int start;  //start point at vlive_ch
    private int end;  //end point at vlive_ch
    private SubmoduleEventListener listener; //event listener

    /**
     * Instantiates a new Request builder.
     */
    public RequestBuilder() {
        urlList = new LinkedList<>();
        submoduleCodeList = new LinkedList<>();
    }

    /**
     * Sets url.
     *
     * @param url the url
     * @return the url
     */
    public RequestBuilder setUrl(String url) {
        urlList.offer(url);

        return this;
    }

    /**
     * Build request.
     *
     * @return the request
     */
    public Request build() throws NullEventListenerException {
        this.setSubmoduleCode();

        if (listener == null) {
            throw new NullEventListenerException();
        } else {
            return new Request(urlList, submoduleCodeList);
        }
    }

    /**
     * Build request.
     *
     * @param start the start
     * @param end   the end
     * @return the request
     */
    public Request build(int start, int end) throws NullEventListenerException {
        this.setSubmoduleCode();

        if (listener == null) {
            throw new NullEventListenerException();
        } else {
            return new Request(urlList, submoduleCodeList, start, end);
        }
    }

    private void setSubmoduleCode() {
        for (String url : urlList) {
            submoduleCodeList.offer(SubmoduleCode.findSubModuleByType(
                    ServiceType.findServiceByURL(url)
            ));
        }
    }

    public RequestBuilder setListener(SubmoduleEventListener listener) {
        this.listener = listener;
        return this;
    }
}
