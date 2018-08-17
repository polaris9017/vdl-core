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
package kr.projectn.vdl.core.frame;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SubmoduleCode {
    MODULE_VLIVE("vlive", Arrays.asList(ServiceType.VLIVE, ServiceType.VLIVE_CHANNEL)),
    MODULE_NAVER("naver", Arrays.asList(ServiceType.NAVER)),
    MODULE_KAKAO("kakao", Arrays.asList(ServiceType.DAUM, ServiceType.DAUMKAKAO, ServiceType.KAKAO)),
    //MODULE_SNS("social", Arrays.asList(ServiceType.FACEBOOK, ServiceType.INSTAGRAM)),
    MODULE_SNS("social", Arrays.asList(ServiceType.INSTAGRAM)),
    MODULE_NONE("not-in-service", Collections.emptyList());

    private String subCode;
    private List<ServiceType> svcList;
    private ServiceType svcType;

    SubmoduleCode(String subCode, List<ServiceType> svcList) {
        this.subCode = subCode;
        this.svcList = svcList;
    }

    public static SubmoduleCode findSubModuleByType(ServiceType svcType) {
        return Arrays.stream(SubmoduleCode.values())
                .filter(sub -> sub.hasSubModule(svcType))
                .findAny()
                .orElse(MODULE_NONE);
    }

    private boolean hasSubModule(ServiceType svcType) {
        this.svcType = svcType;
        return svcList.stream()
                .anyMatch(svc -> svc.equals(svcType));
    }

    public String getSubCode() {
        return subCode;
    }

    public String getSvcType() {
        return svcType.getCode();
    }
}
