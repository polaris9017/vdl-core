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
package kr.projectn.vdl.core.event;

import com.google.common.eventbus.Subscribe;

/**
 * Abstract class for implement SubmoduleEventListener
 * <h2>How to use</h2>
 * <p>To create listener to listen submodule event, </p>
 * <ol>
 * <li>Implement your own listener extending this class except {@linkplain #receive(SubmoduleEvent)}</li>
 * <li>Pass itself to {@code RequestBuilder} instance's
 * {@linkplain kr.projectn.vdl.core.RequestBuilder#setListener(SubmoduleEventListener)}</li>
 * </ol>
 *
 * @since 1.0
 */
public abstract class SubmoduleEventListener {
    protected String subcode;

    /**
     * Method for receiving events from Eventbus
     * @param e Event instance
     */
    @Subscribe
    public void receive(SubmoduleEvent e) {
        subcode = e.getSubmodule();

        switch (e.getCurrentMethod()) {
            case "init":
                onInitPageLoaded(e);
                break;
            case "parse":
                onPageParsed(e);
                break;
            case "fetch":
                onFetchedVideoList(e);
                break;
            case "retrieve":
                onRetrievedMediaSpec(e);
                break;
            case "store":
                onStoredMediaSpec(e);
                break;
            case "error":
                onError(e);
        }
    }

    /*
     * methods handling events received from submodules
     */

    /**
     * handle requestInitPage() method
     */
    public abstract void onInitPageLoaded(SubmoduleEvent e);

    /**
     * handle parsePage() method
     */
    public abstract void onPageParsed(SubmoduleEvent e);

    /**
     * handle FetchVideoList() method
     */
    public abstract void onFetchedVideoList(SubmoduleEvent e);

    /**
     * handle retrieveMediaSpec() method
     */
    public abstract void onRetrievedMediaSpec(SubmoduleEvent e);

    /**
     * handle getFinalMediaSpec() method
     */
    public abstract void onStoredMediaSpec(SubmoduleEvent e);

    /**
     * handle errors
     * @param e thrown event entity
     */
    public abstract void onError(SubmoduleEvent e);
}
