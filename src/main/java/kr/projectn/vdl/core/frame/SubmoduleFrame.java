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
 * The type Submodule frame.
 */
public abstract class SubmoduleFrame {
    /**
     * The Url.
     */
    protected String url;
    /**
     * The Init page.
     */
    protected String initPage;
    /**
     * The Response.
     */
    protected Response response;
    /**
     * The Module str.
     */
    protected String moduleStr;
    /**
     * The Bus.
     */
    protected EventBus bus;

    private SubmoduleEventListener listener;

    private SubmoduleFrame(SubmoduleCode subCode) {
        moduleStr = subCode.getSvcType();
    }

    /**
     * Instantiates a new Submodule frame.
     *
     * @param req the req
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
     * Run response.
     *
     * @return the response
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
     * Request init page.
     */
    protected void requestInitPage() {
        WebClient client = new WebClient();

        bus.post(new SubmoduleEvent(moduleStr, "init"));

        initPage = client.setClientConnection(this.url)
                .request()
                .getAsString();
    }

    /**
     * Parse page.
     */
    protected void parsePage() {
        bus.post(new SubmoduleEvent(moduleStr, "parse"));
    }

    /**
     * Retrieve media spec.
     */
    protected void retrieveMediaSpec() {
        bus.post(new SubmoduleEvent(moduleStr, "retrieve"));
    }

    /**
     * Gets final media spec.
     *
     * @return the final media spec
     */
    protected void getFinalMediaSpec() {
        bus.post(new SubmoduleEvent(moduleStr, "store"));
    }

}