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
package kr.projectn.vdl.core.frame;

import kr.projectn.vdl.core.Response;
import kr.projectn.vdl.core.util.WebClient;

public abstract class SubmoduleFrame {
    protected String url;
    protected String initPage;
    protected Response response;

    private SubmoduleFrame() {
    }

    public SubmoduleFrame(String url) {
        this();
        this.url = url;
        response = new Response();
    }

    public Response run() {
        this.requestInitPage();
        this.parsePage();

        if (response.isEmpty())
            this.retrieveMediaSpec();

        return this.getFinalMediaSpec();
    }

    private void requestInitPage() {
        WebClient client = new WebClient();

        initPage = client.setClientConnection(this.url)
                .request("get")
                .getAsString();
    }

    protected abstract void parsePage();

    protected abstract void retrieveMediaSpec();

    protected abstract Response getFinalMediaSpec();
}