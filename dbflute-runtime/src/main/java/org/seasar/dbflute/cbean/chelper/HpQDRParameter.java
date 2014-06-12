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

import java.util.Date;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.coption.FromToOption;

/**
 * The parameter of (Query)DerivedReferrer.
 * @param <CB> The type of condition-bean.
 * @param <PARAMETER> The type of parameter.
 * @author jflute
 */
public class HpQDRParameter<CB extends ConditionBean, PARAMETER> extends HpQDRProtoParameter<CB, PARAMETER> {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpQDRParameter(String function, SubQuery<CB> subQuery, DerivedReferrerOption option,
            HpQDRSetupper<CB> setupper) {
        super(function, subQuery, option, setupper);
    }

    // ===================================================================================
    //                                                                           Condition
    //                                                                           =========
    /**
     * Set up the operand 'equal' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// If the type is Integer...</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).equal(123); <span style="color: #3F7E5E">// This parameter should be Integer!</span>
     * </pre>
     * @param value The value of parameter. (NotNull) 
     */
    public void equal(PARAMETER value) {
        facadeEqual(value);
    }

    /**
     * Set up the operand 'notEqual' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// If the type is Integer...</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).notEqual(123); <span style="color: #3F7E5E">// This parameter should be Integer!</span>
     * </pre>
     * @param value The value of parameter. (NotNull) 
     */
    public void notEqual(PARAMETER value) {
        facadeNotEqual(value);
    }

    /**
     * Set up the operand 'greaterThan' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// If the type is Integer...</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterThan(123); <span style="color: #3F7E5E">// This parameter should be Integer!</span>
     * </pre>
     * @param value The value of parameter. (NotNull) 
     */
    public void greaterThan(PARAMETER value) {
        facadeGreaterThan(value);
    }

    /**
     * Set up the operand 'lessThan' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// If the type is Integer...</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).lessThan(123); <span style="color: #3F7E5E">// This parameter should be Integer!</span>
     * </pre>
     * @param value The value of parameter. (NotNull) 
     */
    public void lessThan(PARAMETER value) {
        facadeLessThan(value);
    }

    /**
     * Set up the operand 'greaterEqual' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// If the type is Integer...</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterEqual(123); <span style="color: #3F7E5E">// This parameter should be Integer!</span>
     * </pre>
     * @param value The value of parameter. (NotNull) 
     */
    public void greaterEqual(PARAMETER value) {
        facadeGreaterEqual(value);
    }

    /**
     * Set up the operand 'lessEqual' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// If the type is Integer...</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).lessEqual(123); <span style="color: #3F7E5E">// This parameter should be Integer!</span>
     * </pre>
     * @param value The value of parameter. (NotNull) 
     */
    public void lessEqual(PARAMETER value) {
        facadeLessEqual(value);
    }

    /**
     * Set up the operand 'isNull' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice();
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).isNull(); <span style="color: #3F7E5E">// no parameter</span>
     * </pre>
     */
    public void isNull() {
        facadeIsNull();
    }

    /**
     * Set up the operand 'isNull' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice();
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).isNotNull(); <span style="color: #3F7E5E">// no parameter</span>
     * </pre>
     */
    public void isNotNull() {
        facadeIsNotNull();
    }

    /**
     * Set up the operand 'between' and the values of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// If the type is Integer...</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).between(53, 123); <span style="color: #3F7E5E">// This parameter should be Integer!</span>
     * </pre>
     * @param fromValue The 'from' value of parameter. (NotNull) 
     * @param toValue The 'to' value of parameter. (NotNull) 
     */
    public void between(PARAMETER fromValue, PARAMETER toValue) {
        facadeBetween(fromValue, toValue);
    }

    /**
     * Set up the comparison 'DateFromTo' and the values of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).dateFromTo(toTimestamp("2012/03/05"), toTimestamp("2012/03/07"));
     * <span style="color: #3F7E5E">// PURCHASE_DATE between 2012/03/05 00:00:00 and 2012/03/07 23:59:59.999</span>
     * </pre>
     * @param fromDate The 'from' date of parameter. (NullAllowed: if null, from-date condition is ignored) 
     * @param toDate The 'to' date of parameter. (NullAllowed: if null, to-date condition is ignored) 
     */
    public void dateFromTo(Date fromDate, Date toDate) {
        facadeDateFromTo(fromDate, toDate);
    }

    /**
     * Set up the comparison 'FromTo' and the values of parameter. <br />
     * The type of the parameter should be same as the type of target column. <br />
     * If the specified column is date type and has time-parts, you should use java.sql.Timestamp type.
     * <pre>
     * cb.query().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).fromTo(toTimestamp("2012/02/05"), toTimestamp("2012/04/07"), new FromToOption().compareAsMonth());
     * <span style="color: #3F7E5E">// PURCHASE_DATE between 2012/02/01 00:00:00 and 2012/04/30 23:59:59.999</span>
     * </pre>
     * @param fromDate The 'from' date of parameter. (NullAllowed: if null, from-date condition is ignored) 
     * @param toDate The 'to' date of parameter. (NullAllowed: if null, to-date condition is ignored) 
     * @param option The option of from-to. (NotNull)
     */
    public void fromTo(Date fromDate, Date toDate, FromToOption option) {
        facadeFromTo(fromDate, toDate, option);
    }
}
