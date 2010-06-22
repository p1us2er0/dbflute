package org.seasar.dbflute.cbean.chelper;

import java.util.List;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @param <CB> The type of condition-bean for column specification. 
 */
public class HpCalcSpecification<CB extends ConditionBean> implements HpCalculator, HpCalcStatement {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SpecifyQuery<CB> _specifyQuery;
    protected CB _cb;
    protected final List<CalculationElement> _calculationList = DfCollectionUtil.newArrayList();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpCalcSpecification(SpecifyQuery<CB> specifyQuery) {
        _specifyQuery = specifyQuery;
    }

    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    public void specify(CB cb) {
        _specifyQuery.specify(cb);
        _cb = cb; // saves for handling the specified column
    }

    public ColumnInfo getSpecifiedColumnInfo() {
        return _cb.getSqlClause().getSpecifiedColumnInfoAsOne();
    }

    public ColumnRealName getSpecifiedColumnRealName() {
        return _cb.getSqlClause().getSpecifiedColumnRealNameAsOne();
    }

    public ColumnSqlName getSpecifiedColumnSqlName() {
        return _cb.getSqlClause().getSpecifiedColumnSqlNameAsOne();
    }

    // ===================================================================================
    //                                                                          Calculator
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    public HpCalculator plus(Number plusValue) {
        return register(CalculationType.PLUS, plusValue);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator minus(Number minusValue) {
        return register(CalculationType.MINUS, minusValue);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator multiply(Number multiplyValue) {
        return register(CalculationType.MULTIPLY, multiplyValue);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator divide(Number divideValue) {
        return register(CalculationType.DIVIDE, divideValue);
    }

    protected HpCalculator register(CalculationType type, Number value) {
        if (value == null) {
            String msg = "The null value was specified as " + type + ": " + _specifyQuery;
            throw new IllegalArgumentException(msg);
        }
        final CalculationElement calculation = new CalculationElement();
        calculation.setCalculationType(type);
        calculation.setCalculationValue(value);
        _calculationList.add(calculation);
        return this;
    }

    // ===================================================================================
    //                                                                           Statement
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public String buildStatementAsSqlName() {
        return doBuildStatement(false);
    }

    /**
     * {@inheritDoc}
     */
    public String buildStatementAsRealName() {
        return doBuildStatement(true);
    }

    protected String doBuildStatement(boolean real) {
        final String columnExp;
        if (real) {
            final ColumnRealName columnRealName = getSpecifiedColumnRealName();
            columnExp = columnRealName.toString();
        } else {
            final ColumnSqlName columnSqlName = getSpecifiedColumnSqlName();
            columnExp = columnSqlName.toString();
        }
        final List<CalculationElement> calculationList = getCalculationList();
        if (calculationList.isEmpty()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(columnExp);
        int index = 0;
        for (CalculationElement calculation : calculationList) {
            if (index > 0) {
                sb.insert(0, "(").append(")");
            }
            sb.append(" ").append(calculation.getCalculationType().operand());
            sb.append(" ").append(calculation.getCalculationValue());
            ++index;
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public SpecifyQuery<CB> getSpecifyQuery() {
        return _specifyQuery;
    }

    public List<CalculationElement> getCalculationList() {
        return _calculationList;
    }
}