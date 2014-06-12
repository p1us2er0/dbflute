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
import org.seasar.dbflute.cbean.SubQuery;

/**
 * The function for ScalarCondition (the old name: ScalarSubQuery).
 * @param <CB> The type of condition-bean.
 * @author jflute
 */
public class HpSSQFunction<CB extends ConditionBean> extends HpSSQProtoFunction<CB> {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpSSQFunction(HpSSQSetupper<CB> setupper) {
        super(setupper);
    }

    // ===================================================================================
    //                                                                            Function
    //                                                                            ========
    /**
     * Set up the sub query of myself for the scalar 'max'.
     * <pre>
     * cb.query().scalar_Equal().<span style="color: #DD4747">max</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// *Point!</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull)
     * @return The decorator of ScalarCondition. You can use partition-by. (NotNull)
     */
    public HpSSQDecorator<CB> max(SubQuery<CB> subQuery) {
        return facadeMax(subQuery);
    }

    /**
     * Set up the sub query of myself for the scalar 'min'.
     * <pre>
     * cb.query().scalar_Equal().<span style="color: #DD4747">min</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// *Point!</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull)
     * @return The decorator of ScalarCondition. You can use partition-by. (NotNull)
     */
    public HpSSQDecorator<CB> min(SubQuery<CB> subQuery) {
        return facadeMin(subQuery);
    }

    /**
     * Set up the sub query of myself for the scalar 'sum'.
     * <pre>
     * cb.query().scalar_Equal().<span style="color: #DD4747">sum</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// *Point!</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull)
     * @return The decorator of ScalarCondition. You can use partition-by. (NotNull)
     */
    public HpSSQDecorator<CB> sum(SubQuery<CB> subQuery) {
        return facadeSum(subQuery);
    }

    /**
     * Set up the sub query of myself for the scalar 'avg'.
     * <pre>
     * cb.query().scalar_Equal().<span style="color: #DD4747">avg</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// *Point!</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull)
     * @return The decorator of ScalarCondition. You can use partition-by. (NotNull)
     */
    public HpSSQDecorator<CB> avg(SubQuery<CB> subQuery) {
        return facadeAvg(subQuery);
    }
}
