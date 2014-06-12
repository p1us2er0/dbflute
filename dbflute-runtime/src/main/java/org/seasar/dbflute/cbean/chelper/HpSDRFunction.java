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
import org.seasar.dbflute.dbmeta.DBMetaProvider;

/**
 * The function for (Specify)DerivedReferrer.
 * @param <REFERRER_CB> The type of referrer condition-bean.
 * @param <LOCAL_CQ> The type of local condition-query.
 * @author jflute
 */
public class HpSDRFunction<REFERRER_CB extends ConditionBean, LOCAL_CQ extends ConditionQuery> extends
        HpSDRProtoFunction<REFERRER_CB, LOCAL_CQ> {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpSDRFunction(ConditionBean baseCB, LOCAL_CQ localCQ, HpSDRSetupper<REFERRER_CB, LOCAL_CQ> querySetupper,
            DBMetaProvider dbmetaProvider) {
        super(baseCB, localCQ, querySetupper, dbmetaProvider);
    }

    // ===================================================================================
    //                                                                            Function
    //                                                                            ========
    /**
     * Set up the sub query of referrer for the scalar 'count'.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">count</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchaseId</span>(); <span style="color: #3F7E5E">// basically PK to count records</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_purchaseCount</span>);
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void count(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        facadeCount(subQuery, aliasName);
    }

    /**
     * An overload method for count() with an option. So refer to the method's java-doc about basic info.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">count</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchaseId</span>(); <span style="color: #3F7E5E">// basically PK to count records</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_purchaseCount</span>, new DerivedReferrerOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param option The option for DerivedReferrer. For example, you can use a coalesce function. (NotNull)
     */
    public void count(SubQuery<REFERRER_CB> subQuery, String aliasName, DerivedReferrerOption option) {
        facadeCount(subQuery, aliasName, option);
    }

    /**
     * Set up the sub query of referrer for the scalar 'count-distinct'.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">countDistinct</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnProductId</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_productKindCount</span>);
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void countDistinct(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        facadeCountDistinct(subQuery, aliasName);
    }

    /**
     * An overload method for count() with an option. So refer to the method's java-doc about basic info.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">countDistinct</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnProductId</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_productKindCount</span>, new DerivedReferrerOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param option The option for DerivedReferrer. For example, you can use a coalesce function. (NotNull)
     */
    public void countDistinct(SubQuery<REFERRER_CB> subQuery, String aliasName, DerivedReferrerOption option) {
        facadeCountDistinct(subQuery, aliasName, option);
    }

    /**
     * Set up the sub query of referrer for the scalar 'max'.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">max</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchaseDatetime</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_latestPurchaseDatetime</span>);
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void max(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        facadeMax(subQuery, aliasName);
    }

    /**
     * An overload method for max() with an option. So refer to the method's java-doc.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">max</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchaseDatetime</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_latestPurchaseDatetime</span>, new DerivedReferrerOption().<span style="color: #DD4747">coalesce</span>("2011-06-07"));
     * </pre>
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param option The option for DerivedReferrer. For example, you can use a coalesce function. (NotNull)
     */
    public void max(SubQuery<REFERRER_CB> subQuery, String aliasName, DerivedReferrerOption option) {
        facadeMax(subQuery, aliasName, option);
    }

    /**
     * Set up the sub query of referrer for the scalar 'min'.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">min</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchaseDatetime</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_firstPurchaseDatetime</span>);
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void min(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        facadeMin(subQuery, aliasName);
    }

    /**
     * An overload method for min() with an option. So refer to the method's java-doc.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">min</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchaseDatetime</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_firstPurchaseDatetime</span>, new DerivedReferrerOption().<span style="color: #DD4747">coalesce</span>("2011-06-07"));
     * </pre>
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param option The option for DerivedReferrer. For example, you can use a coalesce function. (NotNull)
     */
    public void min(SubQuery<REFERRER_CB> subQuery, String aliasName, DerivedReferrerOption option) {
        facadeMin(subQuery, aliasName, option);
    }

    /**
     * Set up the sub query of referrer for the scalar 'sum'.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">sum</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_purchasePriceSummary</span>);
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void sum(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        facadeSum(subQuery, aliasName);
    }

    /**
     * An overload method for sum() with an option. So refer to the method's java-doc.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">sum</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_purchasePriceSummary</span>, new DerivedReferrerOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre>
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param option The option for DerivedReferrer. For example, you can use a coalesce function. (NotNull)
     */
    public void sum(SubQuery<REFERRER_CB> subQuery, String aliasName, DerivedReferrerOption option) {
        facadeSum(subQuery, aliasName);
    }

    /**
     * Set up the sub query of referrer for the scalar 'avg'.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">avg</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_purchasePriceAverage</span>);
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void avg(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        facadeAvg(subQuery, aliasName);
    }

    /**
     * An overload method for avg() with an option. So refer to the method's java-doc.
     * <pre>
     * cb.specify().derivePurchaseList().<span style="color: #DD4747">avg</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #DD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True(); <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }, Member.<span style="color: #DD4747">ALIAS_purchasePriceAverage</span>, new DerivedReferrerOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre>
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param option The option for DerivedReferrer. For example, you can use a coalesce function. (NotNull)
     */
    public void avg(SubQuery<REFERRER_CB> subQuery, String aliasName, DerivedReferrerOption option) {
        facadeAvg(subQuery, aliasName, option);
    }
}
