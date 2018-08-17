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

import java.util.Optional;

public class Response {
    private Optional<String> resolution;
    private Optional<String> url;
    private Optional<Long> size;
    private Optional<String> title;
    private ResponseStatus status;

    public Response() {
        status = ResponseStatus.NULL_VAL;
    }

    public void setUrl(String url) {
        this.url = Optional.ofNullable(url);
    }

    public void setResolution(String resolution) {
        this.resolution = Optional.ofNullable(resolution);
    }

    public void setSize(Long size) {
        this.size = Optional.ofNullable(size);
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = Optional.ofNullable(title);
    }

    public String getUrl() {
        return url.get();
    }

    public String getResolution() {
        return resolution.orElse("");
    }

    public Long getSize() {
        return size.orElse((long) 0);
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title.orElse("");
    }

    public boolean isEmpty() {
        return url.isPresent();
    }
}
