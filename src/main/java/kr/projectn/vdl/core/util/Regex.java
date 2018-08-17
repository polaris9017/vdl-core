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
package kr.projectn.vdl.core.util;

import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    private String exprStr;
    private String regexStr;
    private String splitStr;
    private Optional<Matcher> mayMatcher;
    private Matcher match;
    private LinkedList<String> splitGroup;
    private LinkedList<String> matchGroup;

    public Regex setExpressionString(String exprStr) {
        this.exprStr = Optional.ofNullable(exprStr).orElse("");
        return this;
    }

    public Regex setRegexString(String regexStr) {
        matchGroup = new LinkedList<>();
        this.regexStr = Optional.ofNullable(regexStr).orElse("(.+)");

        mayMatcher = Optional.ofNullable(match);
        return this;
    }

    public Regex setSplitString(String splitStr) {
        splitGroup = new LinkedList<>();
        this.splitStr = Optional.ofNullable(splitStr).orElse("");
        return this;
    }

    private boolean parse() {

        Pattern pattern = Pattern.compile(regexStr);

        if (mayMatcher.isPresent()) {
            match.reset();
        }

        match = pattern.matcher(exprStr);

        return match.find();
    }

    public boolean split() {
        if (!this.parse())
            return false;

        for (String el : match.group(1).split(splitStr)) {
            splitGroup.offer(el);
        }

        return true;
    }

    public boolean group() {
        if (!(this.parse()))
            return false;

        for (int i = 0; i <= match.groupCount(); i++) {
            matchGroup.offer(match.group(i));
        }

        return true;
    }

    public LinkedList<String> getSplitGroup() {
        return splitGroup;
    }

    public LinkedList<String> getMatchGroup() {
        return matchGroup;
    }
}
