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
package kr.projectn.vdl.core.submodule.kakao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.core.Request;
import kr.projectn.vdl.core.util.Regex;
import kr.projectn.vdl.core.util.WebClient;

public class kakao extends tvpot {
    private String clipid;

    /**
     * Instantiates a new Submodule frame.
     *
     * @param req the req
     */
    public kakao(Request req) {
        super(req);
    }

    protected void requestInitPage() {
        String vid;
        JsonObject kakaoApiJson;
        Regex regex = new Regex();
        WebClient client = new WebClient();

        if (regex.setRegexString("tv\\.kakao\\.com.+cliplink\\/(.*)")
                .setExpressionString(url)
                .group()) {
            clipid = regex.get(1);
        }

        client.setClientConnection("http://tv.kakao.com/api/v1/ft/cliplinks/" + clipid)
                .request();

        kakaoApiJson = new JsonParser().parse(client.getAsString()).getAsJsonObject();

        vid = kakaoApiJson.get("clip").getAsJsonObject().get("vid").getAsString();

        response.setTitle(kakaoApiJson.get("displayTitle").getAsString());

        url = "http://tvpot.daum.net" + "/v/" + vid;

        super.requestInitPage();
    }
}
