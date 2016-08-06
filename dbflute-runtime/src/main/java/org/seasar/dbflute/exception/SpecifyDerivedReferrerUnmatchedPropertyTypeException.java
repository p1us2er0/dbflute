/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.exception;

/**
 * The exception of when the property type of specify-derived-referrer is unmatched with the actual type.
 * @author jflute
 * @since 1.1.0 (2014/10/20 Monday)
 */
public class SpecifyDerivedReferrerUnmatchedPropertyTypeException extends RuntimeException {

    /** The serial version UID for object serialization. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param msg The message of the exception. (NotNull)
     */
    public SpecifyDerivedReferrerUnmatchedPropertyTypeException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * @param msg The message of the exception. (NotNull)
     * @param cause The cause of the exception. (NotNull)
     */
    public SpecifyDerivedReferrerUnmatchedPropertyTypeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}