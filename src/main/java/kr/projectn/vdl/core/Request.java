/**
 * Copyright 2016-2018 qscx9512 <moonrise917@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.projectn.vdl.core;

import kr.projectn.vdl.core.frame.SubmoduleCode;

import java.util.LinkedList;
import java.util.Queue;

public class Request {
    private Queue<String> urlList;
    private Queue<SubmoduleCode> submoduleCodeList;

    private Request() {
        urlList = new LinkedList<>();
        submoduleCodeList = new LinkedList<>();
    }

    public Request(Queue<String> urlList, Queue<SubmoduleCode> submoduleCodeList) {
        this();
        this.urlList = urlList;
        this.submoduleCodeList = submoduleCodeList;
    }

    public boolean isURLListEmpty() {
        return urlList.isEmpty();
    }

    public String getUrl() {
        return urlList.poll();
    }

    public SubmoduleCode getSubmoduleCode() {
        return submoduleCodeList.poll();
    }
}
