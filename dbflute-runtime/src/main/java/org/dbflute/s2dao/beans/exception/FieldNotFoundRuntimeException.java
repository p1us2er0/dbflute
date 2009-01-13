/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.dbflute.s2dao.beans.exception;

import java.lang.reflect.Field;

import org.seasar.framework.exception.SRuntimeException;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class FieldNotFoundRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -2715036865146285893L;

    private Class targetClass;

    private String fieldName;

    /**
     * {@link FieldNotFoundRuntimeException}を作成します。
     * 
     * @param targetClass
     * @param fieldName
     */
    public FieldNotFoundRuntimeException(Class targetClass, String fieldName) {
        super("ESSR0070", new Object[] { targetClass.getName(), fieldName });
        this.targetClass = targetClass;
        this.fieldName = fieldName;
    }

    /**
     * ターゲットの{@link Class}を返します。
     * 
     * @return ターゲットの{@link Class}
     */
    public Class getTargetClass() {
        return targetClass;
    }

    /**
     * フィールド名を返します。
     * 
     * @return
     */
    public String getFieldName() {
        return fieldName;
    }
}