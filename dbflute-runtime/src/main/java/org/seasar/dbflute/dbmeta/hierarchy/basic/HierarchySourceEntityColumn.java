package org.seasar.dbflute.dbmeta.hierarchy.basic;


import org.seasar.dbflute.dbmeta.hierarchy.HierarchySourceColumn;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;

/**
 * @author jflute
 */
public class HierarchySourceEntityColumn implements HierarchySourceColumn {

    protected ColumnInfo columnInfo;

    public HierarchySourceEntityColumn(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public String getColumnName() {
        return columnInfo.getColumnDbName();
    }

    public java.lang.reflect.Method findGetter() {
        return columnInfo.findGetter();
    }
}