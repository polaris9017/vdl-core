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
package kr.projectn.vdl.core.event;

/**
 * Default submodule event listener inherited {@link SubmoduleEventListener}.<br><br>
 * In default, this works nothing, so you can't listen any events from submodule(s).<br>
 * If you want to listen, create your own submodule event listener inheriting {@link SubmoduleEventListener}.
 *
 * @since 1.0
 */
public class DefaultSubmoduleEventListener extends SubmoduleEventListener {


    @Override
    public void onInitPageLoaded() {

    }

    @Override
    public void onPageParsed() {

    }

    @Override
    public void onFetchedVideoList() {

    }

    @Override
    public void onRetrievedMediaSpec() {

    }

    @Override
    public void onStoredMediaSpec() {

    }

    /**
     * Prints error message when error occurred
     *
     * @param e submodule event instance
     */
    @Override
    public void onError(SubmoduleEvent e) {
        System.err.println(e.getErrorMessage());
    }
}
