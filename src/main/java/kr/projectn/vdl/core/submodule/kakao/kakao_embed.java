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
package kr.projectn.vdl.core.submodule.kakao;

import kr.projectn.vdl.core.Request;
import kr.projectn.vdl.core.SubmoduleLoader;
import kr.projectn.vdl.core.frame.SubmoduleFrame;

@Deprecated
public class kakao_embed extends SubmoduleFrame {
    /**
     * Creates new submodule instance with single {@link Request} entity passed from
     * {@link SubmoduleLoader}
     *
     * @param req {@link Request} entity
     */
    public kakao_embed(Request req) {
        super(req);
    }
}
