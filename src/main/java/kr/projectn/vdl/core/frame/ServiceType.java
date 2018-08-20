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
package kr.projectn.vdl.core.frame;

import java.util.Arrays;

public enum ServiceType {
    VLIVE("vlive_vod", "vlive.tv/video"),
    VLIVE_CHANNEL("vlive_ch", "channels.vlive.tv"),
    NAVER("naver", "tv.naver.com"),
    DAUM("tvpot", "tvpot.daum.net"),
    KAKAO("kakao", "tv.kakao.com"),
    DAUMKAKAO("kakao_embed", "kakaotv.daum.net"),
    FACEBOOK("facebook", "facebook.com"),
    INSTAGRAM("instagram", "instagram.com"),
    NONE("none", "");

    private String subCode;
    private String svcUrl;

    ServiceType(String subCode, String svcUrl) {
        this.subCode = subCode;
        this.svcUrl = svcUrl;
    }

    public String getSvcUrl() {
        return svcUrl;
    }

    public String getCode() {
        return subCode;
    }

    public static ServiceType findServiceByURL(String url) {
        return Arrays.stream(ServiceType.values())
                .filter(svc -> url.contains(svc.getSvcUrl()))
                .findAny()
                .orElse(NONE);
    }
}