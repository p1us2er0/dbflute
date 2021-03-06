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
package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 * @since 0.5.9 (2007/12/21 Friday)
 */
public class DfDelimiterDataHandlerImplTest extends PlainTestCase {

    public void test_isUnsupportedEncodingDirectory() throws Exception {
        final DfDelimiterDataHandlerImpl target = new DfDelimiterDataHandlerImpl();
        assertFalse(target.isUnsupportedEncodingDirectory("UTF-8"));
        assertFalse(target.isUnsupportedEncodingDirectory("Windows-31J"));
        assertTrue(target.isUnsupportedEncodingDirectory("UTF-8sss"));
    }
}
