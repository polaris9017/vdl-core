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

import com.comcast.viper.hlsparserj.MediaPlaylist;
import com.comcast.viper.hlsparserj.PlaylistFactory;
import com.comcast.viper.hlsparserj.PlaylistVersion;
import com.comcast.viper.hlsparserj.tags.media.ExtInf;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import kr.projectn.vdl.core.frame.ResponseStatus;
import kr.projectn.vdl.core.util.Regex;
import kr.projectn.vdl.core.util.WebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

public class HLSResponse extends Response {
    private int seq;
    private String playlistUrl;
    private Map<String, String> header;
    private WebClient client;
    private LinkedList<String> param;
    private List<NameValuePair> paramList;
    private Queue<String> videoUrlQueue;
    private boolean isLiveEnd;

    public HLSResponse() {
        super();
        header = new HashMap<>();
        client = new WebClient();
        param = new LinkedList<>();
        paramList = new ArrayList<>();
        videoUrlQueue = new LinkedList<>();
        seq = 0;
        isLiveEnd = false;
    }

    public HLSResponse setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public HLSResponse setVid(String vid) {
        paramList.add(new BasicNameValuePair("videoSeq", vid));
        return this;
    }

    public HLSResponse setParameter(LinkedList<String> param) {
        this.param = param;
        return this;
    }

    public boolean getHLSSegment() {

        int seq_prev = seq; //previous sequence
        String liveJsonStr;
        JsonArray liveParam;
        Regex regex = new Regex();
        MediaPlaylist mediaPlaylist;

        videoUrlQueue.clear();

        if (param.get(2).equals(ResponseStatus.LIVE_END.name())) {
            isLiveEnd = true;
            return false;
        }

        client.setClientConnection("http://www.vlive.tv/video/init/view")
                .setConnectionParameter(paramList);

        for (String key : header.keySet()) {
            client.setHeader(key,
                    header.get(key));
        }

        liveJsonStr = client.request().getAsString();
        if (regex.setRegexString("\"liveStreamInfo\"\\s*:\\s*\"(.*)\",")
                .setExpressionString(liveJsonStr).group()) {
            liveParam = new JsonParser().parse(regex.getMatchGroup().get(1)
                    .replace("\\\"", "\"")).getAsJsonObject()
                    .get("resolutions").getAsJsonArray();

            playlistUrl = liveParam.get(liveParam.size() - 1).getAsJsonObject()
                    .get("cdnUrl").getAsString();

        }

        mediaPlaylist = (MediaPlaylist) PlaylistFactory.parsePlaylist(PlaylistVersion.TWELVE,
                client.setClientConnection(playlistUrl
                        .replace("playlist.m3u8?__gda", "chunklist.m3u8?__agda"))
                        .request().getAsString()
                        .replace(",\n", ","));

        seq = mediaPlaylist.getMediaSequence().getSequenceNumber();

        if (seq < seq_prev + 3) {
            return false;
        } else {
            for (ExtInf stream : mediaPlaylist.getSegments()) {
                if (regex.setRegexString("\\w*+:\\/\\/vlive(?:[^\\/:]*+\\/)*+")
                        .setExpressionString(playlistUrl).group()) {
                    videoUrlQueue.offer(regex.getMatchGroup().get(0) + stream.getTitle());
                }
            }
            return true;
        }
    }

    public boolean isLiveEnd() {
        return isLiveEnd;
    }

    public Queue<String> getVideoUrlQueue() {
        return videoUrlQueue;
    }
}
