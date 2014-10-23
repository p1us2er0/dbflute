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
package org.seasar.dbflute.optional;

/**
 * The function of optional value.
 * <pre>
 * OptionalObject&lt;MemberWebBean&gt; beanOpt = entityOpt.<span style="color: #DD4747">map</span>(member -&gt; {
 *     <span style="color: #3F7E5E">// called if value exists, not called if not present</span>
 *     return new MemberWebBean(member);
 * });
 * </pre>
 * @param <OBJ> The type of wrapped object in optional object.
 * @param <RESULT> The type of result of mapping.
 * @author jflute
 * @since 1.0.5F (2014/05/10 Saturday)
 * @deprecated
 */
public interface OptionalObjectFunction<OBJ, RESULT> extends OptionalThingFunction<OBJ, RESULT> {
}
