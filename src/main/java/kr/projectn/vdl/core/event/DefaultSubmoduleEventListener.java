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
package kr.projectn.vdl.core.event;

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

    @Override
    public void onError(SubmoduleEvent e) {
        System.err.println(e.getErrorMessage());
    }
}
