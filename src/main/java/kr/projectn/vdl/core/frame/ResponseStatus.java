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
package kr.projectn.vdl.core.frame;

/**
 * Enumeration for {@link kr.projectn.vdl.core.Response} status value
 *
 * @since 1.0
 */
public enum ResponseStatus {
    NOERR(""),
    NULL_VAL(""),
    CH_RANGE_ERR("VOD index range set error."),
    LIVE_END("Live broadcasting ended."),
    LIVE_COMING_SOON("Live bradcasting is in preparation."),
    LIVE_CANCELED("Live broadcasting has been ended unexpectedly."),
    PRODUCT_ONLY_APP("You have to buy package."),
    CH_SUBSCRIBE_NEEDED("Does not support subscribe required channel."),
    INSTA_NO_POST("Cannot load post from URL.");

    String msg;

    ResponseStatus(String msg) {
        this.msg = msg;
    }

    /**
     * Returns error message from {@code ResponseStatus} value
     *
     * @return an error message
     */
    public String getErrorMessage() {
        return msg;
    }
}
