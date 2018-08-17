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
/*
 * Response : return m3u8 url
 * -> HLSParser : process HLS file (loop)
 *
 */

package kr.projectn.vdl.core.submodule.vlive;

import kr.projectn.vdl.core.HLSResponse;
import kr.projectn.vdl.core.Response;
import kr.projectn.vdl.core.frame.ServiceType;
import kr.projectn.vdl.core.frame.SubmoduleFrame;
import kr.projectn.vdl.core.util.Regex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class vlive_Realtime extends SubmoduleFrame {
    private Map<String, String> header = new HashMap<String, String>(); //header
    private HLSResponse hlsResponse = new HLSResponse();
    private LinkedList<String> param = new LinkedList<>();
    private String vid;

    public vlive_Realtime(String url) {
        super(url);
        header.put("Content-Type", "application/x-www-form-urlencoded");
    }

    public Response run() {
        return this.getFinalMediaSpec();
    }

    @Override
    protected void parsePage() {
        Regex regex = new Regex();

        if (regex.setRegexString("\\bvlive\\.video\\.init\\(([^)]+)")
                .setExpressionString(initPage)
                .setSplitString("[\\s\\W]*,[\\s\\W]*").split()) {
            for (String el : regex.getSplitGroup()) {
                param.offer(el);
            }
        }

        if (regex.setRegexString(ServiceType.VLIVE.getSvcUrl() + "\\/([\\d]+)")
                .setExpressionString(url).group()) {
            vid = regex.getMatchGroup().get(1);
        }

        if (regex.setRegexString("\"og[^=]*+=\"(\\[[^\"]*+)\"")
                .setExpressionString(initPage).group()) {
            response.setTitle(regex.getMatchGroup().get(1));
        }

        //add header
        header.put("Referer", url);
    }

    @Override
    protected void retrieveMediaSpec() {

    }

    @Override
    protected Response getFinalMediaSpec() {
        hlsResponse.setHeader(header)
                .setVid(vid)
                .setParameter(param);
        return hlsResponse;
    }
}
