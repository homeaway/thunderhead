/* Copyright (c) 2010 HomeAway, Inc.
 * All rights reserved.  http://jmonette/github.io/thunderhead
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.homeaway.aws.thunderhead.model.enums;

import com.google.common.base.Objects;

/**
 * This class defines constants for cloudsearch queries
 *
 * @author jmonette
 */
public enum CloudSearchBoolean {
    AND("and"),
    NOT("not"),
    OR("or");

    private String booleanOperator;
    private CloudSearchBoolean(String booleanOperator) {
        this.booleanOperator = booleanOperator;
    }

    public String getName() {
        return this.booleanOperator;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("booleanOperator", this.booleanOperator)
                .toString();

    }
}
