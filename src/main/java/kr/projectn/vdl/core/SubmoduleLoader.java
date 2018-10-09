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

import kr.projectn.vdl.core.frame.SubmoduleCode;
import kr.projectn.vdl.core.frame.SubmoduleFrame;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Loader class for loading submodule(s)
 * <h2>How to use</h2>
 * <p>To load and run submodule(s), </p>
 * <ol>
 *     <li>Pass {@link Request} instance you created in advance while initializing instance;</li>
 *     <li>Run {@link #run()} method, and this will executes all requested URLs;</li>
 *     <li>Get result of all requests with {@link #getResponseList()}</li>
 * </ol>
 *
 * @since 1.0
 */
public class SubmoduleLoader {

    private Request req;
    private Queue<SubmoduleFrame> requestedSubModule;
    private Queue<Response> responseFinalMediaSpecList;

    private SubmoduleLoader() {
        requestedSubModule = new LinkedList<>();
        responseFinalMediaSpecList = new LinkedList<>();
    }

    /**
     * Creates a new {@code SubmoduleLoader} instance.
     * @param request {@link Request} instance previously created
     */
    public SubmoduleLoader(Request request) throws Exception {
        this();
        this.req = request;
        this.loadModule();
    }

    private void loadModule() throws Exception {
        SubmoduleCode subCode;

        while (!req.isURLListEmpty()) {
            subCode = req.getSubmoduleCode();

            if (!subCode.name().matches("MODULE_NONE")) {
                Class<?> sub = Class.forName("kr.projectn.vdl.core.submodule." +
                        subCode.getSubCode() + "." + subCode.getSvcType());

                /*
                 * taken from https://stackoverflow.com/questions/46393863/what-to-use-instead-of-class-newinstance
                 *
                 * Class.newInstance() has been deprecated since Java 9, so prefer using
                 * Class.getDeclaredConstructor().newInstance() instead.
                 */
                Object subInstance = sub.getDeclaredConstructor(Request.class)
                        .newInstance(new RequestBuilder().setUrl(req.getUrl())
                                .setListener(req.getListener()).build(req.getStart(), req.getEnd()));
                requestedSubModule.offer((SubmoduleFrame) subInstance);
            } else {
                req.getUrl();  //remove url from list
            }
        }
    }

    /**
     * Runs all requested URLs and adds into internal list
     */
    public void run() {

        if (requestedSubModule.isEmpty()) {
            System.err.println("Request does not queued!");
        }

        for (SubmoduleFrame sub : requestedSubModule) {
            responseFinalMediaSpecList.offer(sub.run());
        }

    }

    /**
     * Returns list of result of all requests.
     * @return queue of result of all requests
     */
    public Queue<Response> getResponseList() {
        return responseFinalMediaSpecList;
    }
}
