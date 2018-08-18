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

/*
 * Instagram media JSON structure (August, 2018)
 *
 * Written based on single post page
 *
 * (legend: obj: object , arr: array, str: string)
 *
 *
 * *********************************************************************************************************************
 * single media:
 * entry_data(obj) -> PostPage(arr) : 0 -> graphql(obj) -> shortcode_media(obj) -> video_url / display_url(str)
 *
 * entry_data(obj) -> PostPage(arr) : 0 -> is_video boolean value:
 * true -> video_url // false -> display_url
 * *********************************************************************************************************************
 *
 *
 * *********************************************************************************************************************
 * multiple media:
 * entry_data(obj) -> PostPage(arr) : 0 -> graphql(obj) -> shortcode_media(obj) -> edge_sidecar_to_children(obj) ->
 * edges(arr, 0 ~ n) -> node(obj) -> video_url / display_url(str)
 *
 * entry_data(obj) -> PostPage(arr) : 0 -> (...) -> node(obj) -> is_video boolean value (could be different each media):
 * true -> video_url // false -> display_url
 * *********************************************************************************************************************
 *
 *
 *
 * *********************************************************************************************************************
 * Load instruction:
 *
 * 1. load post page from URL
 * 2. find window._sharedData value from page-embedded javascript (value -> media JSON)
 * 3. check if entry_data(obj) is empty (check post can be loaded from URL)
 * 3-1. if empty, exit // if not empty, go to 4
 * 4. check if edge_sidecar_to_children(obj) (see upper) is exists
 * 4-1. if exists, extract media URL referring multiple media column //
 *      if not exists, extract media URL referring single media column
 * *********************************************************************************************************************
 */

package kr.projectn.vdl.core.submodule.social;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.core.Request;
import kr.projectn.vdl.core.Response;
import kr.projectn.vdl.core.frame.ResponseStatus;
import kr.projectn.vdl.core.frame.SubmoduleFrame;
import kr.projectn.vdl.core.util.Regex;

public class instagram extends SubmoduleFrame {

    private String mediaJsonStr;

    public instagram(Request req) {
        super(req);
    }

    public void parsePage() {
        Regex regex = new Regex();

        if (regex.setRegexString("window\\._sharedData = (\\{.+\\})")
                .setExpressionString(initPage)
                .group()) {
            mediaJsonStr = regex.get(1);
        }
    }


    public void retrieveMediaSpec() {
        JsonObject jsonBody = new JsonParser().parse(mediaJsonStr).getAsJsonObject();
        JsonArray edgeNode;
        JsonObject shortcodeMedia;

        if (!jsonBody.has("entry_data")) {
            response.setStatus(ResponseStatus.INSTA_NO_POST);
            this.getFinalMediaSpec();
        } else {
            jsonBody = jsonBody.get("entry_data").getAsJsonObject();
        }

        if (jsonBody.get("PostPage").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("graphql").getAsJsonObject()
                .get("shortcode_media").getAsJsonObject()
                .has("edge_sidecar_to_children")) {
            edgeNode = jsonBody.get("PostPage").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("graphql").getAsJsonObject()
                    .get("shortcode_media").getAsJsonObject()
                    .get("edge_sidecar_to_children").getAsJsonObject()
                    .get("edges").getAsJsonArray();

            for (JsonElement el : edgeNode) {
                if (el.getAsJsonObject()
                        .get("node").getAsJsonObject()
                        .get("is_video").getAsBoolean()) {
                    response.setUrl(el.getAsJsonObject()
                            .get("node").getAsJsonObject()
                            .get("video_url").getAsString());
                } else {
                    response.setUrl(el.getAsJsonObject()
                            .get("node").getAsJsonObject()
                            .get("display_url").getAsString());
                }
            }
        } else {
            shortcodeMedia = jsonBody.get("PostPage").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("graphql").getAsJsonObject()
                    .get("shortcode_media").getAsJsonObject();
            if (shortcodeMedia.get("is_video").getAsBoolean()) {
                response.setUrl(shortcodeMedia.get("video_url").getAsString());
            } else {
                response.setUrl(shortcodeMedia.get("display_url").getAsString());
            }
        }
    }


    public Response getFinalMediaSpec() {
        response.setSvctype(moduleStr);
        response.setStatus(ResponseStatus.NOERR);
        return response;
    }
}