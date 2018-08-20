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
import kr.projectn.vdl.core.Response;
import kr.projectn.vdl.core.event.SubmoduleEvent;
import kr.projectn.vdl.core.frame.ResponseStatus;
import kr.projectn.vdl.core.frame.SubmoduleFrame;
import kr.projectn.vdl.core.util.Regex;
import kr.projectn.vdl.core.util.WebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class vlive_ch extends SubmoduleFrame {

    private String channelCode;
    private String appJsUrl;
    private String appID;
    private LinkedList<String> urlList;
    private int start;
    private int end;

    public vlive_ch(Request req) {
        super(req);
        urlList = new LinkedList<>();
        start = req.getStart();
        end = req.getEnd();
    }

    public void parsePage() {
        Regex regex = new Regex();
        WebClient client = new WebClient();

        super.parsePage();

        if (regex.setRegexString("https?:\\/\\/channels\\.vlive\\.tv\\/([\\w\\D]+)\\/video")
                .setExpressionString(url)
                .group()) {
            channelCode = regex.get(1);
        }

        if (regex.setRegexString("<script[^>]+src=[\\\\\"\\'](http.+?\\/app\\.js)")
                .setExpressionString(initPage)
                .group()) {
            appJsUrl = regex.get(1);
        }

        if (regex.setRegexString("a\\.VFAN_APP_ID=\"(.+)\",a\\.POSTABLE_COUNTRIES")
                .setExpressionString(client.setClientConnection(appJsUrl)
                        .request()
                        .getAsString())
                .group()) {
            appID = regex.get(1);
        }
    }


    public void retrieveMediaSpec() {

        super.retrieveMediaSpec();

        this.FetchVideoList();

        if (end > urlList.size()) {
            end = urlList.size();
        } else if (start > end) {
            response.setStatus(ResponseStatus.CH_RANGE_ERR);
            return;
        }

        for (int i = start; i <= end; i++) {
            String url = urlList.get(i);

            Response resp = new vlive_vod(
                    new RequestBuilder()
                            .setUrl(url).build()).run();

            response.setUrl(resp.getUrlList().peek());
            response.setTitle(resp.getTitleList().peek());
            response.setSize(resp.getSizeList().peek());
            response.setResolution(resp.getResolutionList().peek());

            for (Response.Subtitle sub : resp.getSubtitleList(url)) {
                response.setSubtitle(url, sub.getLocale(), sub.getSource());
            }
        }

        response.setStatus(ResponseStatus.NOERR);
    }


    protected void getFinalMediaSpec() {
        super.getFinalMediaSpec();

        response.setSvctype(moduleStr);
    }

    private void FetchVideoList() {
        int channelSeq;
        List<NameValuePair> param = new ArrayList<>();
        WebClient client = new WebClient();

        bus.post(new SubmoduleEvent(moduleStr, "fetch"));

        param.add(new BasicNameValuePair("app_id", appID));
        param.add(new BasicNameValuePair("channelCode", channelCode));

        client.setClientConnection("http://api.vfan.vlive.tv/vproxy/channelplus/decodeChannelCode")
                .setConnectionParameter(param)
                .request();

        channelSeq = new JsonParser().parse(client.getAsString()).getAsJsonObject()
                .get("result").getAsJsonObject().get("channelSeq").getAsInt();

        param.clear();

        param.add(new BasicNameValuePair("app_id", appID));
        param.add(new BasicNameValuePair("channelSeq", String.valueOf(channelSeq)));
        param.add(new BasicNameValuePair("maxNumOfRows", String.valueOf(10000)));

        client.setClientConnection("http://api.vfan.vlive.tv/vproxy/channelplus/getChannelVideoList")
                .setConnectionParameter(param)
                .request();

        JsonArray jsonVideoList = new JsonParser().parse(client.getAsString())
                .getAsJsonObject().get("result").getAsJsonObject()
                .get("videoList").getAsJsonArray();

        for (JsonElement el : jsonVideoList) {
            JsonObject obj = el.getAsJsonObject();

            urlList.offer("http://vlive.tv/video/" + obj.get("videoSeq").getAsString());
        }
    }
}
