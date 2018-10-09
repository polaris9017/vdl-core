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
package kr.projectn.vdl.core.submodule.social;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.core.Request;
import kr.projectn.vdl.core.event.SubmoduleEvent;
import kr.projectn.vdl.core.frame.ResponseStatus;
import kr.projectn.vdl.core.frame.SubmoduleFrame;
import kr.projectn.vdl.core.util.Regex;
import kr.projectn.vdl.core.util.WebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class facebook extends SubmoduleFrame {

    private String token;
    private String vid;
    private WebClient client;

    public facebook(Request req) {
        super(req);
        try {
            client = new WebClient();
        } catch (Exception e) {
            bus.post(new SubmoduleEvent(this, "error").setException(e));
        }
    }

    @Override


    protected void requestInitPage() {
        try {
            List<NameValuePair> param = new ArrayList<>();

            param.add(new BasicNameValuePair("client_id", "841023869361760"));
            param.add(new BasicNameValuePair("client_secret", "e742f0e162c7f0cf412c80434f07a95c"));
            param.add(new BasicNameValuePair("grant_type", "client_credentials"));

            token = new JsonParser().parse(client
                    .setClientConnection("https://graph.facebook.com/oauth/access_token")
                    .setConnectionParameter(param)
                    .request()
                    .getAsString())
                    .getAsJsonObject().get("access_token")
                    .getAsString();
            super.requestInitPage();
        } catch (IOException | URISyntaxException e) {
            bus.post(new SubmoduleEvent(this, "error").setException(e));
        }


    }

    @Override
    protected void parsePage() {
        Regex regex = new Regex();

        if (regex.setRegexString("(?<=facebook\\.com\\/).+\\/videos\\/([\\d]+)")
                .setExpressionString(initPage)
                .group()) {
            vid = regex.getMatchGroup().get(1);
        }
    }

    @Override
    protected void retrieveMediaSpec() {
        try {
            List<NameValuePair> param = new ArrayList<>();
            JsonObject jsonObj;

            param.add(new BasicNameValuePair("fields", "source,title"));
            param.add(new BasicNameValuePair("access_token", token));

            client.setClientConnection("https://graph.facebook.com/v2.10/" + vid)
                    .setConnectionParameter(param)
                    .request();

            jsonObj = new JsonParser().parse(client.getAsString()).getAsJsonObject();

            response.setUrl(jsonObj.get("source").getAsString());
            response.setTitle(jsonObj.get("title").getAsString());
            response.setSvctype(moduleStr);
        } catch (IOException | URISyntaxException e) {
            bus.post(new SubmoduleEvent(this, "error").setException(e));
        }
    }

    @Override
    protected void getFinalMediaSpec() {
        response.setStatus(ResponseStatus.NOERR);
    }
}
