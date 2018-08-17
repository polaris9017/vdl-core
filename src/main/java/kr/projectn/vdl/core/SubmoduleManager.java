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

public class SubmoduleManager {

    private Request req;
    private Queue<SubmoduleFrame> requestedSubModule;
    private Queue<Response> responseFinalMediaSpecList;

    private SubmoduleManager() {
        requestedSubModule = new LinkedList<>();
        responseFinalMediaSpecList = new LinkedList<>();
    }

    public SubmoduleManager(Request request) {
        this();
        this.req = request;
        this.loadModule();
    }

    private void loadModule() {
        SubmoduleCode subCode;

        while (!req.isURLListEmpty()) {
            try {
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
                            .newInstance(new RequestBuilder().setUrl(req.getUrl()).build(req.getStart(), req.getEnd()));
                    requestedSubModule.offer((SubmoduleFrame) subInstance);
                } else {
                    req.getUrl();  //remove url from list
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean run() {

        if (requestedSubModule.isEmpty()) {
            System.err.println("Request does not queued!");
            return false;
        }

        for (SubmoduleFrame sub : requestedSubModule) {
            responseFinalMediaSpecList.offer(sub.run());
        }

        return true;
    }

    public Queue<Response> getResponseList() {
        return responseFinalMediaSpecList;
    }
}
