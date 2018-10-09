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
 * Response : return m3u8 url
 * -> HLSParser : process HLS file (loop)
 *
 */

package kr.projectn.vdl.core.submodule.vlive;

import kr.projectn.vdl.core.HLSResponse;
import kr.projectn.vdl.core.Request;
import kr.projectn.vdl.core.event.SubmoduleEvent;
import kr.projectn.vdl.core.frame.ResponseStatus;
import kr.projectn.vdl.core.frame.ServiceType;
import kr.projectn.vdl.core.frame.SubmoduleFrame;
import kr.projectn.vdl.core.util.Regex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class vlive_Realtime extends SubmoduleFrame {
    private Map<String, String> header = new HashMap<String, String>(); //header
    private HLSResponse hlsResponse;
    private LinkedList<String> param = new LinkedList<>();
    private String vid;

    public vlive_Realtime(Request req) {
        super(req);
        moduleStr = ServiceType.VLIVE_REALTIME.getCode();  //Change submodule code to vlive live broadcasting
        header.put("Content-Type", "application/x-www-form-urlencoded");
    }


    protected void parsePage() {
        Regex regex = new Regex();
        try {
            hlsResponse = new HLSResponse();
        } catch (Exception e) {
            bus.post(new SubmoduleEvent(this, "error").setException(e));
        }

        super.parsePage();

        if (regex.setRegexString("\\bvlive\\.video\\.init\\(([^)]+)")
                .setExpressionString(initPage)
                .setSplitString("[\\s\\W]*,[\\s\\W]*").split()) {
            for (String el : regex.getSplitGroup()) {
                param.offer(el);
            }
        }

        if (regex.setRegexString(ServiceType.VLIVE.getSvcUrl() + "\\/([\\d]+)")
                .setExpressionString(url).group()) {
            vid = regex.get(1);
        }

        if (regex.setRegexString("\"og[^=]*+=\"(\\[[^\"]*+)\"")
                .setExpressionString(initPage).group()) {
            hlsResponse.setTitle(regex.get(1));
        }

        hlsResponse.setStatus(ResponseStatus.NOERR);

        //add header
        header.put("Referer", url);
    }


    protected void retrieveMediaSpec() {

    }


    protected void getFinalMediaSpec() {
        super.getFinalMediaSpec();

        hlsResponse.setHeader(header)
                .setVid(vid)
                .setParameter(param);
        hlsResponse.setSvctype(moduleStr);
    }
}
