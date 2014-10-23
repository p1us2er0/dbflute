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
package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;

/**
 * The set-upper for (Specify)DerivedReferrer.
 * @param <REFERRER_CB> The type of referrer condition-bean.
 * @param <LOCAL_CQ> The type of local condition-query.
 * @author jflute
 */
public interface HpSDRSetupper<REFERRER_CB extends ConditionBean, LOCAL_CQ extends ConditionQuery> {

    /**
     * Set up the clause for (Query)DerivedReferrer.
     * @param function The expression of function to derive referrer value. (NotNull)
     * @param subQuery The sub-query to derive. (NotNull) 
     * @param cq The condition-query of local table. (NotNull)
     * @param aliasName The alias name to set the derived value to entity. (NotNull)
     * @param option The option of DerivedReferrer. (NotNull)
     */
    void setup(String function, SubQuery<REFERRER_CB> subQuery, LOCAL_CQ cq, String aliasName, DerivedReferrerOption option);
}
