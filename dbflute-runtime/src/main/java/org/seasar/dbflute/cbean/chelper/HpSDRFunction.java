package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerInvalidAliasNameException;
import org.seasar.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.dbflute.util.Srl;

/**
 * The function of specify-derived-referrer.
 * @author jflute
 * @param <REFERRER_CB> The type of referrer condition-bean.
 * @param <LOCAL_CQ> The type of local condition-query.
 */
public class HpSDRFunction<REFERRER_CB extends ConditionBean, LOCAL_CQ extends ConditionQuery> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ConditionBean _baseCB;
    protected final LOCAL_CQ _localCQ;
    protected final HpSDRSetupper<REFERRER_CB, LOCAL_CQ> _querySetupper;
    protected final DBMetaProvider _dbmetaProvider;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpSDRFunction(ConditionBean baseCB, LOCAL_CQ localCQ, HpSDRSetupper<REFERRER_CB, LOCAL_CQ> querySetupper,
            DBMetaProvider dbmetaProvider) {
        _baseCB = baseCB;
        _localCQ = localCQ;
        _querySetupper = querySetupper;
        _dbmetaProvider = dbmetaProvider;
    }

    // ===================================================================================
    //                                                                            Function
    //                                                                            ========
    /**
     * Set up the sub query of referrer for the scalar 'count'.
     * <pre>
     * cb.specify().derivePurchaseList().count(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseId(); // *Point! (Basically PK)
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"PAID_PURCHASE_COUNT\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void count(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        count(subQuery, aliasName, null);
    }

    /**
     * An overload method for count(). So refer to the method's java-doc.
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param coalesce The alternate value when the function result is null. (Nullable: not use coalesce function)
     */
    public void count(SubQuery<REFERRER_CB> subQuery, String aliasName, Object coalesce) {
        assertAliasName(aliasName);
        _querySetupper.setup("count", coalesce, subQuery, _localCQ, filterAliasName(aliasName));
    }

    /**
     * Set up the sub query of referrer for the scalar 'count(with distinct)'.
     * <pre>
     * cb.specify().derivePurchaseList().countDistinct(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnProductId(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"PAID_PURCHASE_PRODUCT_KIND_COUNT\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void countDistinct(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        countDistinct(subQuery, aliasName, null);
    }

    /**
     * An overload method for countDistinct(). So refer to the method's java-doc.
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param coalesce The alternate value when the function result is null. (Nullable: not use coalesce function)
     */
    public void countDistinct(SubQuery<REFERRER_CB> subQuery, String aliasName, Object coalesce) {
        assertAliasName(aliasName);
        _querySetupper.setup("count(distinct", coalesce, subQuery, _localCQ, filterAliasName(aliasName));
    }

    /**
     * Set up the sub query of referrer for the scalar 'max'.
     * <pre>
     * cb.specify().derivePurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"LATEST_PURCHASE_DATETIME\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void max(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        max(subQuery, aliasName, null);
    }

    /**
     * An overload method for max(). So refer to the method's java-doc.
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param coalesce The alternate value when the function result is null. (Nullable: not use coalesce function)
     */
    public void max(SubQuery<REFERRER_CB> subQuery, String aliasName, Object coalesce) {
        assertAliasName(aliasName);
        _querySetupper.setup("max", coalesce, subQuery, _localCQ, filterAliasName(aliasName));
    }

    /**
     * Set up the sub query of referrer for the scalar 'min'.
     * <pre>
     * cb.specify().derivePurchaseList().min(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"LATEST_PURCHASE_DATETIME\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void min(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        min(subQuery, aliasName, null);
    }

    /**
     * An overload method for min(). So refer to the method's java-doc.
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param coalesce The alternate value when the function result is null. (Nullable: not use coalesce function)
     */
    public void min(SubQuery<REFERRER_CB> subQuery, String aliasName, Object coalesce) {
        assertAliasName(aliasName);
        _querySetupper.setup("min", coalesce, subQuery, _localCQ, filterAliasName(aliasName));
    }

    /**
     * Set up the sub query of referrer for the scalar 'sum'.
     * <pre>
     * cb.specify().derivePurchaseList().sum(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"SUMMARY_PURCHASE_PRICE\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void sum(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        sum(subQuery, aliasName, null);
    }

    /**
     * An overload method for sum(). So refer to the method's java-doc.
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param coalesce The alternate value when the function result is null. (Nullable: not use coalesce function)
     */
    public void sum(SubQuery<REFERRER_CB> subQuery, String aliasName, Object coalesce) {
        assertAliasName(aliasName);
        _querySetupper.setup("sum", coalesce, subQuery, _localCQ, filterAliasName(aliasName));
    }

    /**
     * Set up the sub query of referrer for the scalar 'avg'.
     * <pre>
     * cb.specify().derivePurchaseList().avg(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"AVERAGE_PURCHASE_PRICE\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void avg(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        avg(subQuery, aliasName, null);
    }

    /**
     * An overload method for avg(). So refer to the method's java-doc.
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     * @param coalesce The alternate value when the function result is null. (Nullable: not use coalesce function)
     */
    public void avg(SubQuery<REFERRER_CB> subQuery, String aliasName, Object coalesce) {
        assertAliasName(aliasName);
        _querySetupper.setup("avg", coalesce, subQuery, _localCQ, filterAliasName(aliasName));
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String filterAliasName(String aliasName) {
        return aliasName != null ? aliasName.trim() : null;
    }

    protected void assertAliasName(String aliasName) {
        if (isPurposeNullAlias()) {
            if (aliasName != null) {
                String msg = "The aliasName should be null in the purpose: " + _baseCB.getPurpose();
                throw new SpecifyDerivedReferrerInvalidAliasNameException(msg);
            }
        } else { // normal
            if (Srl.is_Null_or_TrimmedEmpty(aliasName)) {
                throwSpecifyDerivedReferrerInvalidAliasNameException();
            }
        }
        // *this check was moved to runtime (when creating a behavior command)
        //String tableDbName = _baseCB.getTableDbName();
        //DBMeta dbmeta = _dbmetaProvider.provideDBMetaChecked(tableDbName);
        //Method[] methods = dbmeta.getEntityType().getMethods();
        //String targetMethodName = "set" + replaceString(aliasName, "_", "").toLowerCase();
        //boolean existsSetterMethod = false;
        //for (Method method : methods) {
        //    if (!method.getName().startsWith("set")) {
        //        continue;
        //    }
        //    if (targetMethodName.equals(method.getName().toLowerCase())) {
        //        existsSetterMethod = true;
        //        break;
        //    }
        //}
        //if (!existsSetterMethod) {
        //    throwSpecifyDerivedReferrerEntityPropertyNotFoundException(aliasName, dbmeta.getEntityType());
        //}
    }

    protected boolean isPurposeNullAlias() {
        return _baseCB.getPurpose().equals(HpCBPurpose.COLUMN_QUERY);
    }

    protected void throwSpecifyDerivedReferrerInvalidAliasNameException() {
        createCBExThrower().throwSpecifyDerivedReferrerInvalidAliasNameException(_localCQ);
    }

    //protected void throwSpecifyDerivedReferrerEntityPropertyNotFoundException(String aliasName, Class<?> entityType) {
    //    createCBExThrower().throwSpecifyDerivedReferrerEntityPropertyNotFoundException(aliasName, entityType);
    //}

    //protected String replaceString(String text, String fromText, String toText) {
    //    return Srl.replace(text, fromText, toText);
    //}

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }
}
