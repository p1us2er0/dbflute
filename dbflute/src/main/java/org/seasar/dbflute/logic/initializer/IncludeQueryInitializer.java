package org.seasar.dbflute.logic.initializer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.properties.DfIncludeQueryProperties;
import org.seasar.dbflute.properties.assistant.DfTableFinder;

/**
 * @author jflute
 * @since 0.7.3 (2008/05/30 Friday)
 */
public class IncludeQueryInitializer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(IncludeQueryInitializer.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfIncludeQueryProperties includeQueryProperties;

    protected DfTableFinder tableFinder;

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void initializeIncludeQuery() {
        _log.debug("/=============================");
        _log.debug("...Initializing include query.");
        final Map<String, Map<String, Map<String, List<String>>>> map = includeQueryProperties.getIncludeQueryMap();
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            _log.debug(key);
            final Map<String, Map<String, List<String>>> queryElementMap = map.get(key);
            final Set<String> queryElementKeySet = queryElementMap.keySet();
            for (String queryElementKey : queryElementKeySet) {
                _log.debug("    " + queryElementKey);
                final Map<String, List<String>> tableElementMap = queryElementMap.get(queryElementKey);
                final Set<String> tableElementKeySet = tableElementMap.keySet();
                for (String tableName : tableElementKeySet) {
                    _log.debug("        " + tableName);
                    final Table targetTable = tableFinder.findTable(tableName);
                    if (targetTable == null) {
                        throwIncludeQueryTableNotFoundException(queryElementKey, tableName, map);
                    }
                    List<String> columnNameList = null;
                    try {
                        columnNameList = tableElementMap.get(tableName);
                    } catch (ClassCastException e) {
                        throwIncludeQueryNotListColumnSpecificationException(queryElementKey, tableName, map, e);
                    }
                    for (String columnName : columnNameList) {
                        _log.debug("            " + columnName);
                        final Column targetColumn = targetTable.getColumn(columnName);
                        if (targetColumn == null) {
                            throwIncludeQueryColumnNotFoundException(queryElementKey, tableName, columnName, map);
                        }
                    }
                }
            }
        }
        _log.debug("========/");
    }

    protected void throwIncludeQueryTableNotFoundException(String queryElementKey, String tableName,
            Map<String, Map<String, Map<String, List<String>>>> map) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The table was Not Found in includeQueryMap!" + ln();
        msg = msg + ln();
        msg = msg + "[Query Type]" + ln() + queryElementKey + ln();
        msg = msg + ln();
        msg = msg + "[Table Name]" + ln() + tableName + ln();
        msg = msg + ln();
        msg = msg + "[Include Query Map]" + ln() + map + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IncludeQueryTableNotFoundException(msg);
    }

    protected void throwIncludeQueryNotListColumnSpecificationException(String queryElementKey, String tableName,
            Map<String, Map<String, Map<String, List<String>>>> map, RuntimeException e) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The column specification of the table was Not List Type in includeQueryMap!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You shuold specify them this way:" + ln();
        msg = msg + "    --------------------------------------------" + ln();
        msg = msg + "    " + tableName + " = list:{XXX_ID ; XXX_NAME}" + ln();
        msg = msg + "    --------------------------------------------" + ln();
        msg = msg + ln();
        msg = msg + "[Query Type]" + ln() + queryElementKey + ln();
        msg = msg + ln();
        msg = msg + "[Table Name]" + ln() + tableName + ln();
        msg = msg + ln();
        msg = msg + "[Include Query Map]" + ln() + map + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IncludeQueryNotListColumnSpecificationException(msg, e);
    }

    protected void throwIncludeQueryColumnNotFoundException(String queryElementKey, String tableName,
            String columnName, Map<String, Map<String, Map<String, List<String>>>> map) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The column was Not Found in includeQueryMap!" + ln();
        msg = msg + ln();
        msg = msg + "[Query Type]" + ln() + queryElementKey + ln();
        msg = msg + ln();
        msg = msg + "[Table Name]" + ln() + tableName + ln();
        msg = msg + ln();
        msg = msg + "[Column Name]" + ln() + columnName + ln();
        msg = msg + ln();
        msg = msg + "[Include Query Map]" + ln() + map + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IncludeQueryColumnNotFoundException(msg);
    }

    protected static class IncludeQueryTableNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public IncludeQueryTableNotFoundException(String msg) {
            super(msg);
        }
    }

    protected static class IncludeQueryNotListColumnSpecificationException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public IncludeQueryNotListColumnSpecificationException(String msg, RuntimeException e) {
            super(msg, e);
        }
    }

    protected static class IncludeQueryColumnNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public IncludeQueryColumnNotFoundException(String msg) {
            super(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    public String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfIncludeQueryProperties getIncludeQueryProperties() {
        return includeQueryProperties;
    }

    public void setIncludeQueryProperties(DfIncludeQueryProperties includeQueryProperties) {
        this.includeQueryProperties = includeQueryProperties;
    }

    public DfTableFinder getTableFinder() {
        return tableFinder;
    }

    public void setTableFinder(DfTableFinder tableFinder) {
        this.tableFinder = tableFinder;
    }
}
