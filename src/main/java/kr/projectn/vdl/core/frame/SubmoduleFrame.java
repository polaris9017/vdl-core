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
package kr.projectn.vdl.core.frame;

import com.google.common.eventbus.EventBus;
import kr.projectn.vdl.core.Request;
import kr.projectn.vdl.core.Response;
import kr.projectn.vdl.core.event.SubmoduleEvent;
import kr.projectn.vdl.core.event.SubmoduleEventListener;
import kr.projectn.vdl.core.util.WebClient;

/**
 * Abstract class for building submodules.
 * <h2>How to use</h2>
 * <ol>
 *     <li>Inherit this class to build submodule frame</li>
 *     <li>Implement all method except {@link #requestInitPage()} and {@link #getUrl()}.
 *         In that case, you should call {@code super.{Current method}()} to post event through {@link EventBus}
 *     </li>
 *     <li>Execute {@link #run()} and get {@link Response} for result</li>
 * </ol>
 *
 * @since 1.0
 */
public abstract class SubmoduleFrame {

    protected String url;
    protected String initPage;
    protected Response response;
    protected String moduleStr;
    protected EventBus bus;
    protected SubmoduleEventListener listener;

    private SubmoduleFrame(SubmoduleCode subCode) {
        moduleStr = subCode.getSvcType();
    }

    /**
     * Creates new submodule instance with single {@link Request} entity passed from
     * {@link kr.projectn.vdl.core.SubmoduleLoader}
     * @param req {@link Request} entity
     */
    public SubmoduleFrame(Request req) {
        this(req.getSubmoduleCode());
        this.url = req.getUrl();
        response = new Response();
        bus = new EventBus();
        listener = req.getListener();
        bus.register(listener);
    }

    /**
     * Run all methods and returns {@link Response} entity<br><br>
     * In default, methods will be executed  {@link #requestInitPage()}, {@link #parsePage()},
     * {@link #retrieveMediaSpec()}, {@link #getFinalMediaSpec()} in sequence.
     * @return {@link Response} entity contains video metadata(media URL, resolution, etc.)
     */
    public Response run() {

        this.requestInitPage();
        this.parsePage();

        if (response.isEmpty())
            this.retrieveMediaSpec();
        this.getFinalMediaSpec();

        return response;
    }

    /**
     * Requests video view page body. <br><br>
     * Store plain {@code String} of page body into {@code initPage} variable after request.
     */
    protected void requestInitPage() {
        try {
            WebClient client = new WebClient();

            bus.post(new SubmoduleEvent(this, "init"));

            initPage = client.setClientConnection(this.url)
                    .request()
                    .getAsString();
        } catch (Exception e) {
            bus.post(new SubmoduleEvent(this, "error").setException(e));
        }
    }

    /**
     * Parses page body to prepare for retrieving video metadata. <br><br>
     * This will may vary to submodule
     */
    protected void parsePage() {
        bus.post(new SubmoduleEvent(this, "parse"));
    }

    /**
     * Retrieves video metadata.<br><br>
     * This will may vary to submodule
     */
    protected void retrieveMediaSpec() {
        bus.post(new SubmoduleEvent(this, "retrieve"));
    }

    /**
     * Stores final video metadata and status
     */
    protected void getFinalMediaSpec() {
        bus.post(new SubmoduleEvent(this, "store"));
    }

    /**
     * Returns currently working URL
     *
     * @return current URL
     */
    public String getUrl() {
        return url;
    }
}