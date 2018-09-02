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
package kr.projectn.vdl.core.submodule.vlive;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.core.Request;
import kr.projectn.vdl.core.RequestBuilder;
import kr.projectn.vdl.core.event.SubmoduleEvent;
import kr.projectn.vdl.core.frame.ResponseStatus;
import kr.projectn.vdl.core.frame.SubmoduleFrame;
import kr.projectn.vdl.core.util.Regex;
import kr.projectn.vdl.core.util.WebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class vlive_vod extends SubmoduleFrame {

    private String status;
    private String vid_long;
    private String key;
    private Regex regex;

    public vlive_vod(Request req) {
        super(req);
    }


    protected void parsePage() {
        regex = new Regex();

        super.parsePage();

        if (regex.setRegexString("\\bvlive\\.video\\.init\\(([^)]+)\\)")
                .setSplitString("[\\s\\W]*,[\\s\\W]*")
                .setExpressionString(initPage)
                .split()
        ) {
            status = regex.get(2).replace("\"", "");
            vid_long = regex.get(5).replace("\"", "");
            key = regex.get(6).replace("\"", "");
        }

        switch (status) {
            case "LIVE_ON_AIR":
            case "BIG_EVENT_ON_AIR":
                response = new vlive_Realtime(new RequestBuilder().setUrl(url).build()).run();
                break;
            case "VOD_ON_AIR":
            case "BIG_EVENT_INTRO":
                break;
            case "LIVE_END":
                response.setStatus(ResponseStatus.LIVE_END);
                break;
            case "COMING_SOON":
                response.setStatus(ResponseStatus.LIVE_COMING_SOON);
                break;
            case "CANCELED":
                response.setStatus(ResponseStatus.LIVE_CANCELED);
                break;
            case "PRODUCT_ONLY_APP":
                response.setStatus(ResponseStatus.PRODUCT_ONLY_APP);
        }

        if (!response.isEmpty()) {
            bus.post(new SubmoduleEvent(this, "error")
                    .setErrorMessage(response.getStatus().getErrorMessage()));
        }
    }


    protected void retrieveMediaSpec() {
        WebClient client = new WebClient();
        List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
        String title;
        Stack<String> videoRes = new Stack<>();
        Stack<String> cdnUrl = new Stack<>();
        Stack<Long> fSize = new Stack<>();

        super.retrieveMediaSpec();

        reqParam.add(new BasicNameValuePair("videoId", vid_long));
        reqParam.add(new BasicNameValuePair("key", key));
        reqParam.add(new BasicNameValuePair("ptc", "http"));
        reqParam.add(new BasicNameValuePair("doct", "json"));
        reqParam.add(new BasicNameValuePair("cpt", "vtt"));

        client.setClientConnection("http://global.apis.naver.com/rmcnmv/rmcnmv/vod_play_videoInfo.json")
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

        if (mediaJsonObj.has("captions")) {
            JsonArray subtitleList = mediaJsonObj.get("captions")
                    .getAsJsonObject().get("list").getAsJsonArray();

            for (JsonElement it : subtitleList) {
                JsonObject obj = it.getAsJsonObject();

                response.setSubtitle(obj.get("locale").getAsString(), obj.get("source").getAsString());
            }
        } else {
            response.setSubtitle("", "");
        }


        response.setUrl(cdnUrl.pop());
        response.setResolution(videoRes.pop());
        response.setSize(fSize.pop());
    }


    protected void getFinalMediaSpec() {

        super.getFinalMediaSpec();

        response.setStatus(ResponseStatus.NOERR);
        response.setSvctype(moduleStr);
    }
}
