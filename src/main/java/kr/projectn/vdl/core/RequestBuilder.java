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

import com.google.common.base.Objects;
import kr.projectn.vdl.core.event.DefaultSubmoduleEventListener;
import kr.projectn.vdl.core.event.SubmoduleEventListener;
import kr.projectn.vdl.core.frame.ServiceType;
import kr.projectn.vdl.core.frame.SubmoduleCode;

import java.util.LinkedList;
import java.util.Queue;

public class RequestBuilder {

    private Queue<String> urlList;
    private Queue<SubmoduleCode> submoduleCodeList;
    private int start;  //start point at vlive_ch
    private int end;  //end point at vlive_ch
    private SubmoduleEventListener listener; //event listener

    public RequestBuilder() {
        urlList = new LinkedList<>();
        submoduleCodeList = new LinkedList<>();
    }

    public RequestBuilder setUrl(String url) {
        urlList.offer(url);

        return this;
    }

    public Request build() {
        this.setSubmoduleCode();

        if (Objects.equal(listener, null))
            listener = new DefaultSubmoduleEventListener();

        return new Request(urlList, submoduleCodeList, listener);
    }

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

    public RequestBuilder setListener(SubmoduleEventListener listener) {
        this.listener = listener;
        return this;
    }
}
