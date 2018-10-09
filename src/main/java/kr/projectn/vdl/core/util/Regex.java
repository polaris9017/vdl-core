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
package kr.projectn.vdl.core.util;

import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex Util class wrapping {@link Pattern} and {@link Matcher}
 *
 * @since 1.0
 */
public class Regex {
    private String exprStr;
    private String regexStr;
    private String splitStr;
    private Optional<Matcher> mayMatcher;
    private Matcher match;
    private LinkedList<String> splitGroup;
    private LinkedList<String> matchGroup;

    /**
     * Creates new {@link Regex}.
     */
    public Regex() {
        splitGroup = new LinkedList<>();
        matchGroup = new LinkedList<>();
    }

    /**
     * Set string which want to parse
     *
     * @param exprStr string to parse
     * @return {@link Regex} entity contains string to parse
     */
    public Regex setExpressionString(String exprStr) {
        this.exprStr = Optional.ofNullable(exprStr).orElse("");
        return this;
    }

    /**
     * Set express pattern string to parse
     * @param regexStr pattern string
     * @return {@link Regex} entity contains pattern string
     */
    public Regex setRegexString(String regexStr) {
        matchGroup = new LinkedList<>();
        this.regexStr = Optional.ofNullable(regexStr).orElse("(.+)");

        mayMatcher = Optional.ofNullable(match);
        return this;
    }

    /**
     * Set split delimiter string used at {@link #split()}
     * @param splitStr split delimiter string
     * @return {@link Regex} entity contains split delimiter
     */
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

    /**
     * Splits previously inputted string with delimiter pattern into linked list.<br>
     * <br>
     * <p>
     *     This method will do parsing and splitting string and returns splitting result. <br>
     *     {@code true} will be returned if successfully done splitting, else will return {@code false}.
     * </p>
     *
     * <h2>Notice</h2>
     * <p>
     *     This method should be run just once.
     * </p>
     *
     * @return splitting result as boolean
     */
    public boolean split() {
        if (!this.parse())
            return false;

        for (String el : match.group(1).split(splitStr)) {
            splitGroup.offer(el);
        }

        return true;
    }

    /**
     * Parse previously inputted string with express pattern and group captured subsequences into linked list.<br>
     * <br>
     * <p>
     *     This method will do regex string and groups string into matching subsequences and returns grouping result. <br>
     *     {@code true} will be returned if successfully done grouping, else will return {@code false}.
     * </p>
     *
     * <h2>Notice</h2>
     * <p>
     *     This method should be run just once.
     * </p>
     *
     * @return grouping result as boolean
     */
    public boolean group() {
        if (!(this.parse()))
            return false;

        for (int i = 0; i <= match.groupCount(); i++) {
            matchGroup.offer(match.group(i));
        }

        return true;
    }

    /**
     * Returns splitted string list from {@link #split()}
     * @return splitted string list
     */
    public LinkedList<String> getSplitGroup() {
        return splitGroup;
    }

    /**
     * Returns matched subsequences string list from {@link #group()}
     * @return subsequences string list
     */
    public LinkedList<String> getMatchGroup() {
        return matchGroup;
    }

    /**
     * Returns input subsequence captured during grouping or splitting operation.<br><br>
     * Group zero denotes the entire pattern, so the expression m.get(0) is equivalent to returning {@link #exprStr}<br><br>
     * If you done {@link #split()}, elements will be loaded from splitted string list, else will be loaded from
     * grouped string list.
     *
     * @param index index of list
     * @return captured subsequence
     */

    /*
      TODO: 2018-09-03 handle exception which index inputted abnormaly such as bigger than list size
     */
    public String get(int index) {
        if (splitGroup.isEmpty()) {
            return matchGroup.get(index);
        }
        return splitGroup.get(index);
    }
}
