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
package kr.projectn.vdl.core.submodule.naver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.core.Request;
import kr.projectn.vdl.core.Response;
import kr.projectn.vdl.core.frame.ResponseStatus;
import kr.projectn.vdl.core.frame.SubmoduleFrame;
import kr.projectn.vdl.core.frame.SubmoduleMessageEvent;
import kr.projectn.vdl.core.util.Regex;
import kr.projectn.vdl.core.util.WebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class naver extends SubmoduleFrame {
    private String mid;
    private String key;
    private Regex regex;

    /**
     * Instantiates a new Submodule frame.
     *
     * @param req the req
     */
    public naver(Request req) {
        super(req);
    }

    protected void parsePage() {
        regex = new Regex();

        bus.post(new SubmoduleMessageEvent(moduleStr, Thread.currentThread().getStackTrace()[1].getMethodName()));

        if (regex.setRegexString("var rmcPlayer = new nhn\\.rmcnmv\\.RMCVideoPlayer\\(\\\"(.+?)\\\", \\\"(.+?)\\\"")
                .setExpressionString(initPage)
                .split()
        ) {
            mid = regex.getMatchGroup().get(1);
            key = regex.getMatchGroup().get(2);
        }
    }


    protected void retrieveMediaSpec() {
        WebClient client = new WebClient();
        List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
        String title;
        Stack<String> videoRes = new Stack<>();
        Stack<String> cdnUrl = new Stack<>();
        Stack<Long> fSize = new Stack<>();

        bus.post(new SubmoduleMessageEvent(moduleStr, Thread.currentThread().getStackTrace()[1].getMethodName()));

        reqParam.add(new BasicNameValuePair("key", key));

        client.setClientConnection("http://play.rmcnmv.naver.com/vod/play/v2.0/" + mid)
                .setConnectionParameter(reqParam);

        JsonObject mediaJsonObj = new JsonParser().parse(
                client.request().getAsString()
        ).getAsJsonObject();

        if (!(title = mediaJsonObj.get("meta").getAsJsonObject()
                .get("subject").getAsString()).isEmpty()) {
            response.setTitle(title);
        } else {
            if (regex.setRegexString("og\\:title.+(\\[[^\"]*+)")
                    .setExpressionString(initPage)
                    .group()) {
                response.setTitle(regex.getMatchGroup().get(1));
            }
        }

        JsonArray videoSpecList = mediaJsonObj.get("videos")
                .getAsJsonObject().getAsJsonArray("list");

        for (JsonElement it : videoSpecList) {
            JsonObject obj = it.getAsJsonObject();

            videoRes.push(obj.get("encodingOption").getAsJsonObject()
                    .get("name").getAsString());
            cdnUrl.push(obj.get("source").getAsString());
            fSize.push(obj.get("size").getAsLong());
        }

        response.setUrl(cdnUrl.pop());
        response.setResolution(videoRes.pop());
        response.setSize(fSize.pop());

    }


    protected Response getFinalMediaSpec() {
        bus.post(new SubmoduleMessageEvent(moduleStr, Thread.currentThread().getStackTrace()[1].getMethodName()));

        response.setStatus(ResponseStatus.NOERR);
        response.setSvctype(moduleStr);
        return response;
    }
}
