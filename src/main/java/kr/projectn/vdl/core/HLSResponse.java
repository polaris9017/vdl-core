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
package kr.projectn.vdl.core;

import kr.projectn.vdl.core.frame.ResponseStatus;
import kr.projectn.vdl.core.util.WebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

public class HLSResponse extends Response {
    private Map<String, String> header;
    private String vid;
    private WebClient client;
    private LinkedList<String> param;
    private List<NameValuePair> paramList;


    public HLSResponse() {
        super();
        header = new HashMap<>();
        client = new WebClient();
        param = new LinkedList<>();
        paramList = new ArrayList<>();
    }

    public HLSResponse setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public HLSResponse setVid(String vid) {
        this.vid = vid;
        paramList.add(new BasicNameValuePair("videoSeq", vid));
        return this;
    }

    public HLSResponse setParameter(LinkedList<String> param) {
        this.param = param;
        return this;
    }

    public boolean getHLSSegment() {

        if (param.get(2).equals(ResponseStatus.LIVE_END.name()))
            return false;

        client.setClientConnection("http://www.vlive.tv/video/init/view")
                .setConnectionParameter(paramList);

        for (String key : header.keySet()) {
            client.setHeader(key,
                    header.get(key));
        }


        return true;
    }
}
