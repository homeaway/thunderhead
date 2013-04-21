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

package com.homeaway.aws.thunderhead.model.builder;
import com.homeaway.aws.thunderhead.model.enums.CloudSearchBoolean;

/**
 * Representation of a cloudsearch query string.
 *
 * @author svanderworth
 */
public abstract class CloudSearchQuery {

    // The string representation of this query, to be lazy-loaded whenever getQueryString() is called
    private String stringRepresentation = null;

    /**
     * A basic key:'value' cloudsearch expression, where the value is a String
     *
     * @param key   the key
     * @param value the value (do not wrap in single quotes)
     * @return a representation of the expression for this key and value.  <br/>
     *         {@code getQueryString()} on the resulting CloudSearchQuery will result in a String in the following
     *         format: key:'value' <br/>
     *         The value is automatically wrapped in single quotes, which need not be specified in the {@code value}
     *         argument. <br/>
     *         For example, <code>match("name", "Susan").getQueryString()</code> returns the String
     *         {@code "name:'Susan'"}
     */
    public static CloudSearchQuery match(String key, String value) {
        return new BasicStringCloudSearchExpression(key, value);
    }

    /**
     * A basic key:value cloudsearch expression, where the value is an int
     *
     * @param key   the key
     * @param value the value
     * @return a representation of the expression for this key and value.  <br/>
     *         {@code getQueryString()} on the resulting CloudSearchQuery will result in a String in the following
     *         format: key:value <br/>
     *         For example, <code>match("count", 1).getQueryString()</code> returns the String {@code "count:1"}
     */
    public static CloudSearchQuery match(String key, int value) {
        return new BasicIntCloudSearchExpression(key, value);
    }

    /**
     * Logical {@code AND} of any number of {@code CloudSearchQueries}. <br/>
     * {@code getQueryString()} on the resulting CloudSearchQuery will result in a String in the following format:
     * (and query1 query2 ... queryN) <br/>
     * for example, <code>and(match("name", "Susan"), match("count", 1)).getQueryString()</code> returns the
     * String {@code "(and name:'Susan' count:1)"}
     *
     * @param operands the CloudSearchQueries to be {@code AND}'d
     * @return a query representing the logical AND of all the {@code operands}
     */
    public static CloudSearchQuery and(CloudSearchQuery... operands) {
        return new NAryCloudSearchExpression(CloudSearchBoolean.AND, operands);
    }

    /**
     * Logical {@code OR} of any number of {@code CloudSearchQueries}. <br/>
     * {@code getQueryString()} on the resulting CloudSearchQuery will result in a String in the following format:
     * (or query1 query2 ... queryN) <br/>
     * for example, <code>or(match("name", "Susan"), match("count", 1)).getQueryString()</code> returns the
     * String {@code "(or name:'Susan' count:1)"}
     *
     * @param operands the CloudSearchQueries to be {@code OR}'d
     * @return a query representing the logical OR of all the {@code operands}
     */
    public static CloudSearchQuery or(CloudSearchQuery... operands) {
        return new NAryCloudSearchExpression(CloudSearchBoolean.OR, operands);
    }

    /**
     * Logical {@code NOT} of one {@code CloudSearchQuery}. <br/>
     * {@code getQueryString()} on the resulting CloudSearchQuery will result in a String in the following format:
     * (not query) <br/>
     * for example, <code>not(match("count", 1).getQueryString()</code> returns the String
     * {@code "(not count:1)"}
     *
     * @param operand the CloudSearchQuery to be {@code NOT}'d
     * @return a query representing the logical NOT of {@code operand}
     */
    public static CloudSearchQuery not(CloudSearchQuery operand) {
        return new NAryCloudSearchExpression(CloudSearchBoolean.NOT, operand);
    }

    /**
     * Append this {@code CloudSearchQuery}'s query string (the valid query string for use by cloudsearch) to the
     * StringBuilder specified. <br/>
     * This is a helper for {@code getQueryString()}, and subclasses MUST implement this method correctly in order for
     * query strings to be generated correctly.
     *
     * @param stringBuilder the builder to append to
     */
    protected abstract void appendSelf(StringBuilder stringBuilder);

    /**
     * Returns the string representation of this query, for use by cloudsearch.
     *
     * @return the query string to give to cloudsearch
     */
    public final String getQueryString() {
        // if we've already generated the query string, just return it
        if (stringRepresentation != null) {
            return stringRepresentation;
        }

        /* otherwise, do the actual computation to generate the query string.  
         * Just pass the same StringBuilder down through the chain of nested queries to avoid creating many builders 
         * when rendering a single complex query */
        StringBuilder stringBuilder = new StringBuilder();
        appendSelf(stringBuilder);
        stringRepresentation = stringBuilder.toString();
        return stringRepresentation;
    }

    /**
     * A string describing this CloudSearchQuery.  NOT guaranteed to be a valid query string for cloudsearch;
     * use {@code getQueryString()} to generate the valid query string for use by cloudsearch.
     *
     * @return a string describing this CloudSearchQuery
     */
    @Override
    public String toString() {
        return getQueryString();
    }

    /**
     * Compares a CloudSearchQuery to another object.  The result is {@code true} if and only if {@code o} is not null
     * and is a CloudSearchQuery object whose string representation is equal to the string representation of this
     * CloudSearchQuery.
     *
     * @param o the object to compare this against
     * @return {@code true} if the given object produces a query string that is equivalent to this query's string
     *         representation; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        /* Since a CloudSearchQuery is a representation of a query string, two CloudSearchQueries are logically 
         * equivalent if their getQueryString() methods produce equal strings, regardless of the implementation beneath. 
         */
        return (o instanceof CloudSearchQuery) && this.getQueryString().equals(((CloudSearchQuery) o).getQueryString());
    }

    /**
     * Returns a hash code for this CloudSearchQuery.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        /* Since a CloudSearchQuery is a representation of a query string, all of its meaningful information is 
         * encapsulated in its string representation, regardless of the implementation beneath. */
        return getQueryString().hashCode();
    }

    /**
     * A simple cloudsearch expression: a key-value pair with a String value, rendered as key:'value'
     */
    private static class BasicStringCloudSearchExpression extends CloudSearchQuery {
        private final String key;
        private final String value;

        public BasicStringCloudSearchExpression(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Appends the key and value to the specified builder, in the form: key:'value'
         *
         * @param stringBuilder the builder to append to
         */
        @Override
        protected void appendSelf(StringBuilder stringBuilder) {
            stringBuilder.append(key).append(":").append("'").append(value).append("'");
        }
    }

    /**
     * A simple cloudsearch expression: a key-value pair with an int value, rendered as key:value
     */
    private static class BasicIntCloudSearchExpression extends CloudSearchQuery {
        private final String key;
        private final int value;

        public BasicIntCloudSearchExpression(String key, int value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Appends the key and value to the specified builder, in the form: key:value
         *
         * @param stringBuilder the builder to append to
         */
        protected void appendSelf(StringBuilder stringBuilder) {
            stringBuilder.append(key).append(":").append(value);
        }
    }

    /**
     * The n-ary form of a cloudsearch query, which consists of an operator and n operands, which can be more n-ary
     * queries or simple key:value queries.
     */
    private static class NAryCloudSearchExpression extends CloudSearchQuery {

        private final CloudSearchQuery[] operands;
        private final CloudSearchBoolean operator;

        public NAryCloudSearchExpression(CloudSearchBoolean operator, CloudSearchQuery... operands) {
            this.operator = operator;
            this.operands = operands;
        }

        /**
         * Appends the operator and operands to the specified builder, in the form:
         * (operator operand1 operand2 ... operandN)
         *
         * @param stringBuilder the builder to append to
         */
        protected void appendSelf(StringBuilder stringBuilder) {

            stringBuilder.append("(");

            stringBuilder.append(operator.getName());

            for (CloudSearchQuery query : operands) {
                stringBuilder.append(" ");
                query.appendSelf(stringBuilder);
            }

            stringBuilder.append(")");
        }
    }
}
