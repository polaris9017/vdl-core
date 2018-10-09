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
package kr.projectn.vdl.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import kr.projectn.vdl.core.frame.ResponseStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * Returns result information from request procedure
 *
 * @since 1.0
 */
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
    private ListMultimap<String, Subtitle> subtitleList;
    private Queue<Long> sizeList;
    private String svctype;
    private ResponseStatus status;

    /**
     * Creates a new {@code Response} instance<br>
     * All lists are empty in default.
     */
    public Response() {
        status = ResponseStatus.NULL_VAL;
        resolutionList = new LinkedList<>();
        urlList = new LinkedList<>();
        titleList = new LinkedList<>();
        sizeList = new LinkedList<>();
        subtitleList = ArrayListMultimap.create();
    }

    /**
     * Add URL to list.<br>
     * Parameter should be not null, and will not be listed if parameter is null.
     * @param url URL string
     */
    public void setUrl(String url) {
        this.url_op = Optional.ofNullable(url);
        url_op.ifPresent(el -> urlList.offer(el));
    }

    /**
     * Add submodule service type code(i.e vlive_vod) to list.<br>
     * Parameter should be not null, and will not be listed if parameter is null.
     * @param svctype service type code
     */
    public void setSvctype(String svctype) {
        this.svctype_op = Optional.ofNullable(svctype);
        svctype = svctype_op.orElse("");
    }

    /**
     * Add video resolution string to list. <br>
     * Parameter should be not null, and will not be listed if parameter is null.
     * @param resolution video resolution string
     */
    public void setResolution(String resolution) {
        this.resolution_op = Optional.ofNullable(resolution);
        resolution_op.ifPresent(el -> resolutionList.offer(el));
    }

    /**
     * Add video file size to list.<br>
     * Parameter should be not null, and will not be listed if parameter is null.
     * @param size file size
     */
    public void setSize(Long size) {
        this.size_op = Optional.ofNullable(size);
        size_op.ifPresent(el -> sizeList.offer(el));
    }

    /**
     * Add status of request procedure.<br>
     * Most of cases, {@code NOERR} value will be stored if there was no error.
     * @param status request status
     */
    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    /**
     * Add video title(also be file name) to list <br>
     * Parameter should be not null, and will not be listed if parameter is null.
     * @param title video title string
     */
    public void setTitle(String title) {
        this.title_op = Optional.ofNullable(title);
        title_op.ifPresent(el -> titleList.offer(el));
    }

    /**
     * Add subtitle locale and source to list<br>
     * This method is designed to use at single video URL. For multiple video URL,
     * use {@link #setSubtitle(String, String, String)}<br>
     * Parameter should be not null, and will not be listed if parameter is null.
     * @param locale subtitle locale
     * @param source subtitle source URL
     */
    public void setSubtitle(String locale, String source) {
        subtitleLocale_op = Optional.ofNullable(locale);
        subtitleSource_op = Optional.ofNullable(source);

        subtitleList.put(urlList.peek(), new SubtitleBuilder()
                .setLocale(subtitleLocale_op.orElse(""))
                .setSource(subtitleSource_op.orElse(""))
                .build());
    }

    /**
     * Add subtitle locale and source to list<br>
     * This method is designed to use at multiple video URL.<br>
     * Parameter should be not null, and will not be listed if parameter is null.
     * @param url video URL
     * @param locale subtitle locale
     * @param source subtitle source URL
     */
    public void setSubtitle(String url, String locale, String source) {
        subtitleLocale_op = Optional.ofNullable(locale);
        subtitleSource_op = Optional.ofNullable(source);

        subtitleList.put(url, new SubtitleBuilder()
                .setLocale(subtitleLocale_op.orElse(""))
                .setSource(subtitleSource_op.orElse(""))
                .build());
    }

    /**
     * Returns request status code
     */
    public ResponseStatus getStatus() {
        return status;
    }

    /**
     * Returns list of video resolutions. <br><br>
     * All elements of list are arranged as order of {@code Request}.
     */
    public Queue<String> getResolutionList() {
        return resolutionList;
    }

    /**
     * Returns list of video resolutions. <br><br>
     * All elements of list are arranged as order of {@code Request}.
     */
    public Queue<String> getUrlList() {
        return urlList;
    }

    /**
     * Returns list of video title. <br><br>
     * All elements of list are arranged as order of {@code Request}.
     */
    public Queue<String> getTitleList() {
        return titleList;
    }

    /**
     * Returns list of video size. <br><br>
     * All elements of list are arranged as order of {@code Request}.
     */
    public Queue<Long> getSizeList() {
        return sizeList;
    }

    /**
     * Returns submodule service type code
     */
    public String getSvctype() {
        return svctype;
    }

    /**
     * Returns list of video subtitles. <br><br>
     * All elements of list are arranged as order of {@code Request}.
     * @param url video URL to find subtitle
     */
    public List<Subtitle> getSubtitleList(String url) {
        return subtitleList.get(url);
    }

    /**
     * Determines whether any of list is empty.
     */
    public boolean isEmpty() {
        return status.equals(ResponseStatus.NULL_VAL);
    }

    /**
     * Empties all of lists.
     */
    public void empty() {
        resolutionList.clear();
        urlList.clear();
        titleList.clear();
        sizeList.clear();
        svctype = "";
    }

    /**
     * Container class for storing subtitle metadata.
     */
    public class Subtitle {
        private String locale;
        private String source;

        /**
         * Creates a new {@code Subtitle} class with parameters. <br><br>
         * You can create it yourself, but recommend creating with {@link SubtitleBuilder} instance.<br><br>
         * If subtitle has no locale info, put 'en-US' to {@code locale} parameter.
         * @param locale subtitle locale
         * @param source subtitle source URL
         */
        public Subtitle(String locale, String source) {
            this.locale = locale;
            this.source = source;
        }

        /**
         * Returns subtitle locale info
         */
        public String getLocale() {
            return locale;
        }

        /**
         * Returns subtitle source URL
         */
        public String getSource() {
            return source;
        }
    }

    private class SubtitleBuilder {
        private String locale;
        private String source;

        public SubtitleBuilder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public SubtitleBuilder setSource(String source) {
            this.source = source;
            return this;
        }

        public Subtitle build() {
            return new Subtitle(locale, source);
        }
    }
}
