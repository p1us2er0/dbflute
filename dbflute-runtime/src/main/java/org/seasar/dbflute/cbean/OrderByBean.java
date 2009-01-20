package org.seasar.dbflute.cbean;

import org.seasar.dbflute.cbean.sqlclause.OrderByClause;

/**
 * The order-by-bean as interface.
 * 
 * @author jflute
 */
public interface OrderByBean extends SelectResource {

    /**
     * Get sql component of order-by clause.
     * 
     * @return Sql component of order-by clause. (NotNull)
     */
    public OrderByClause getSqlComponentOfOrderByClause();

    /**
     * Get order-by clause.
     * 
     * @return Order-by clause. (NotNull)
     */
    public String getOrderByClause();

    /**
     * Clear order-by.
     * 
     * @return this. (NotNull)
     */
    public OrderByBean clearOrderBy();

    /**
     * Ignore order-by.
     * 
     * @return this. (NotNull)
     */
    public OrderByBean ignoreOrderBy();

    /**
     * Make order-by effective.
     * 
     * @return this. (NotNull)
     */
    public OrderByBean makeOrderByEffective();
}
