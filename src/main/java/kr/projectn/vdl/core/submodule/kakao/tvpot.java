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
package kr.projectn.vdl.core.submodule.kakao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class tvpot extends SubmoduleFrame {
    private String vid;

    /**
     * Instantiates a new Submodule frame.
     *
     * @param req the req
     */
    public tvpot(Request req) {
        super(req);
    }

    @Override
    protected void parsePage() {
        Regex regex = new Regex();

        super.parsePage();

        if (regex.setRegexString("tvpot\\.daum\\.net.+v\\/(.+)")
                .setExpressionString(url)
                .group()) {
            vid = regex.get(1);
        }
    }

    @Override
    protected void retrieveMediaSpec() {
        try {
            JsonArray metaData;
            JsonObject jsonObject;
            WebClient client = new WebClient();
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            Stack<String> videoRes = new Stack<>();
            Stack<String> cdnUrl = new Stack<>();
            Stack<Long> fSize = new Stack<>();

            super.retrieveMediaSpec();

            param.add(new BasicNameValuePair("vid", vid));
            param.add(new BasicNameValuePair("dte_type", "WEB"));


            client.setClientConnection("http://videofarm.daum.net/controller/api/closed/v1_2/IntegratedMovieData.json")
                    .setConnectionParameter(param)
                    .request();

            jsonObject = new JsonParser().parse(client.getAsString()).getAsJsonObject();
            metaData = jsonObject.get("output_list").getAsJsonObject()
                    .get("output_list").getAsJsonArray();

            for (JsonElement it : metaData) {
                JsonObject obj = it.getAsJsonObject();

                videoRes.push(obj.get("label").getAsString());
                fSize.push(obj.get("filesize").getAsLong());

                param.add(new BasicNameValuePair("vid", vid));
                param.add(new BasicNameValuePair("profile", obj.get("profile").getAsString()));

                XmlHandler handler = new XmlHandler(
                        "http://videofarm.daum.net/controller/api/open/v1_2/MovieLocation.apixml"
                        , param);

                handler.parse();

                cdnUrl.push(handler.getUrl());

                param.clear();
            }

            if (response.getTitleList().isEmpty()) {
                param.add(new BasicNameValuePair("vid", vid));

                client.setClientConnection("http://tvpot.daum.net/clip/ClipInfoXml.do")
                        .setConnectionParameter(param)
                        .request();

                XmlHandler handler = new XmlHandler(client.getAsString());

                handler.parse();

                response.setTitle(handler.getTitle());
            }

            response.setUrl(cdnUrl.pop());
            response.setResolution(videoRes.pop());
            response.setSize(fSize.pop());
        } catch (Exception e) {
            bus.post(new SubmoduleEvent(this, "error").setException(e));
        }
    }

    @Override
    protected void getFinalMediaSpec() {
        super.getFinalMediaSpec();
        response.setStatus(ResponseStatus.NOERR);
        response.setSvctype(moduleStr);
    }

    private class XmlHandler extends DefaultHandler {
        private SAXParserFactory factory;  //Parser factory
        private SAXParser parser; //XML Parser
        private String doc;
        private String startTagName, endTagName;

        private StringBuffer buf = new StringBuffer();

        private String retUrl;
        private String retTitle;

        public XmlHandler() {

        }

        public XmlHandler(String doc) {
            super();
            this.doc = doc;
            try {
                factory = SAXParserFactory.newInstance();
                parser = factory.newSAXParser();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        public XmlHandler(String url, List<NameValuePair> param) throws Exception {
            this(new WebClient().setClientConnection(url)
                    .setConnectionParameter(param)
                    .request()
                    .getAsString());
        }


        public void startDocument() {

        }

        public void endDocument() {

        }

        public void startElement(String url, String name, String elementName, Attributes attr) {
            startTagName = elementName;
            buf.setLength(0);
        }

        public void characters(char[] str, int start, int len) {
            buf.append(str, start, len);
            if (this.startTagName.equals("url")) {
                retUrl = buf.toString().trim();
            } else if (this.startTagName.equals("TITLE")) {
                retTitle = buf.toString().trim();
            }
        }

        public void endElement(String url, String localName, String name) {
            endTagName = name;
        }

        public void parse() {
            try {
                parser.parse(new InputSource(new StringReader(doc)), this);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        public String getUrl() {
            return retUrl;
        }

        public String getTitle() {
            return retTitle;
        }
    }
}
