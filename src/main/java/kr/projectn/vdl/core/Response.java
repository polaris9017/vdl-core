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

import kr.projectn.vdl.core.frame.ResponseStatus;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class Response {
    private Optional<String> resolution_op;
    private Optional<String> url_op;
    private Optional<Long> size_op;
    private Optional<String> title_op;
    private Optional<String> svctype_op;
    private Optional<String> subtitleLocale_op;
    private Optional<String> subtitleSource_op;
    private Queue<String> resolutionList;
    private Queue<String> urlList;
    private Queue<String> titleList;
    private Queue<Subtitle> subtitleList;
    private Queue<Long> sizeList;
    private String svctype;
    private ResponseStatus status;

    public Response() {
        status = ResponseStatus.NULL_VAL;
        resolutionList = new LinkedList<>();
        urlList = new LinkedList<>();
        titleList = new LinkedList<>();
        sizeList = new LinkedList<>();
        subtitleList = new LinkedList<>();
    }

    public void setUrl(String url) {
        this.url_op = Optional.ofNullable(url);
        url_op.ifPresent(el -> urlList.offer(el));
    }

    public void setSvctype(String svctype) {
        this.svctype_op = Optional.ofNullable(svctype);
        svctype = svctype_op.orElse("");
    }

    public void setResolution(String resolution) {
        this.resolution_op = Optional.ofNullable(resolution);
        resolution_op.ifPresent(el -> resolutionList.offer(el));
    }

    public void setSize(Long size) {
        this.size_op = Optional.ofNullable(size);
        size_op.ifPresent(el -> sizeList.offer(el));
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title_op = Optional.ofNullable(title);
        title_op.ifPresent(el -> titleList.offer(el));
    }

    public void setSubtitle(int index, String locale, String source) {
        subtitleLocale_op = Optional.ofNullable(locale);
        subtitleSource_op = Optional.ofNullable(source);

        subtitleList.offer(new SubtitleBuilder()
                .setIndex(index)
                .setLocale(subtitleLocale_op.orElse(""))
                .setSource(subtitleSource_op.orElse(""))
                .build());
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public Queue<String> getResolutionList() {
        return resolutionList;
    }

    public Queue<String> getUrlList() {
        return urlList;
    }

    public Queue<String> getTitleList() {
        return titleList;
    }

    public Queue<Long> getSizeList() {
        return sizeList;
    }

    public String getSvctype() {
        return svctype;
    }

    public Queue<Subtitle> getSubtitleList() {
        return subtitleList;
    }

    public boolean isEmpty() {
        return status.equals(ResponseStatus.NULL_VAL);
    }

    public void empty() {
        resolutionList.clear();
        urlList.clear();
        titleList.clear();
        sizeList.clear();
        svctype = "";
    }

    public class Subtitle {
        private int index;
        private String locale;
        private String source;

        public Subtitle(int index, String locale, String source) {
            this.index = index;
            this.locale = locale;
            this.source = source;
        }

        public int getIndex() {
            return index;
        }

        public String getLocale() {
            return locale;
        }

        public String getSource() {
            return source;
        }
    }

    private class SubtitleBuilder {
        private int index;
        private String locale;
        private String source;

        public SubtitleBuilder setIndex(int index) {
            this.index = index;
            return this;
        }

        public SubtitleBuilder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public SubtitleBuilder setSource(String source) {
            this.source = source;
            return this;
        }

        public Subtitle build() {
            return new Subtitle(index, locale, source);
        }
    }
}
