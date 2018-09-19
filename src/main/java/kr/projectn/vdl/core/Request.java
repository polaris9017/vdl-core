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
import kr.projectn.vdl.core.frame.SubmoduleCode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Class for contain request list queue.<br>
 * It cannot be created itself, so create with {@link RequestBuilder}.
 *
 * @since 1.0
 */
public class Request {
    private Queue<String> urlList;
    private Queue<SubmoduleCode> submoduleCodeList;
    private int start;  //start point at vlive_ch
    private int end;  //end point at vlive_ch
    private SubmoduleEventListener listener; //event listener

    private Request() {
        urlList = new LinkedList<>();
        submoduleCodeList = new LinkedList<>();
    }

    /**
     * Create a new {@code Request} with URL list (handled at {@link Request})
     * @param urlList URL list
     * @param submoduleCodeList submodule identification code list (handled at {@link RequestBuilder})
     */
    private Request(Queue<String> urlList, Queue<SubmoduleCode> submoduleCodeList) {
        this();
        this.urlList = urlList;
        this.submoduleCodeList = submoduleCodeList;
    }

    /**
     * Create a new {@code Request} with URL list with range (handled at {@link Request})
     * @param urlList URL list
     * @param submoduleCodeList submodule identification code list (handled at {@link RequestBuilder})
     * @param start start index
     * @param end end index
     */
    private Request(Queue<String> urlList, Queue<SubmoduleCode> submoduleCodeList, int start, int end) {
        this(urlList, submoduleCodeList);
        this.start = start;
        this.end = end;
    }

    /**
     * Create a new {@code Request} with URL list with listener (handled at {@link RequestBuilder})
     * @param urlList URL list
     * @param submoduleCodeList submodule identification code list (handled at {@link RequestBuilder})
     * @param listener listener instance
     */
    public Request(Queue<String> urlList, Queue<SubmoduleCode> submoduleCodeList, SubmoduleEventListener listener) {
        this(urlList, submoduleCodeList);
        this.listener = listener;
    }

    /**
     * Create a new {@code Request} with URL list with range and listener (handled at {@link RequestBuilder})
     * @param urlList URL list
     * @param submoduleCodeList submodule identification code list (handled at {@link RequestBuilder})
     * @param listener listener instance
     * @param start start index
     * @param end end index
     */
    public Request(Queue<String> urlList, Queue<SubmoduleCode> submoduleCodeList, SubmoduleEventListener listener,
                   int start, int end) {
        this(urlList, submoduleCodeList, start, end);

        this.listener = listener;
    }

    /**
     * Returns if URL list is empty
     * @return {@code true} if list is empty
     */
    public boolean isURLListEmpty() {
        return urlList.isEmpty();
    }

    /**
     * Returns URL from internal list (handled at {@link SubmoduleLoader})
     * @return a URL
     */
    public String getUrl() {
        return urlList.poll();
    }

    /**
     * Returns submodule identification code from internal list (handled at {@link SubmoduleLoader})
     * @return submodule identification code
     */
    public SubmoduleCode getSubmoduleCode() {
        return submoduleCodeList.poll();
    }

    /**
     * Returns start index (handled at {@link SubmoduleLoader})
     * @return start index
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns end index (handled at {@link SubmoduleLoader})
     * @return end index
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns {@link com.google.common.eventbus.EventBus} event listener (handled at {@link SubmoduleLoader})
     * @return event listener
     */
    public SubmoduleEventListener getListener() {
        return listener;
    }
}
