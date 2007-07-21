/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author jflute
 */
public class DfTableNameHandler extends DfAbstractMetaDataHandler {

    private static final Log _log = LogFactory.getLog(DfTableNameHandler.class);

    /**
     * Get all the table names in the current database that are not
     * system tables.
     * 
     * @param dbMeta JDBC database metadata.
     * @return The list of all the table meta info in a database.
     * @throws SQLException
     */
    public List<DfTableMetaInfo> getTableNameList(DatabaseMetaData dbMeta, String schemaName) throws SQLException {
        // /---------------------------------------------------- [My Extension]
        // Get DatabaseTypes from ContextProperties.
        // These are the entity types we want from the database
        final String[] types = getDatabaseTypeStringArray();
        logDatabaseTypes(types);
        // -------------------/

        final List<DfTableMetaInfo> tableList = new ArrayList<DfTableMetaInfo>();
        ResultSet resultSet = null;
        try {
            resultSet = dbMeta.getTables(null, schemaName, "%", types);
            while (resultSet.next()) {
                final String tableName = resultSet.getString(3);
                final String tableType = resultSet.getString(4);
                final String tableSchema = resultSet.getString("TABLE_SCHEM");
                final String tableComment = resultSet.getString("REMARKS");

                if (isTableExcept(tableName)) {
                    _log.debug("$ isTableExcept(" + tableName + ") == true");
                    continue;
                }
                if (isOracle() && tableName.startsWith("BIN$")) {
                    _log.debug("$ isTableExcept(" + tableName + ") == true {Forced because the database is Oracle!}");
                    continue;
                }

                final DfTableMetaInfo tableMetaInfo = new DfTableMetaInfo();
                tableMetaInfo.setTableName(tableName);
                tableMetaInfo.setTableType(tableType);
                tableMetaInfo.setTableSchema(tableSchema);
                tableMetaInfo.setTableComment(tableComment);
                tableList.add(tableMetaInfo);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }

        resolveSameNameTable(tableList);
        return tableList;
    }

    /**
     * Resolve same name table.
     * <pre>
     * 同じ名前のTableが存在するTableに関しては、
     * それを示すFlagをtrueにする。
     * </pre>
     * @param tableMetaInfoList The list of table meta info. (NotNull)
     */
    protected void resolveSameNameTable(final List<DfTableMetaInfo> tableMetaInfoList) {
        final Set<String> tableNameSet = new HashSet<String>();
        final Set<String> sameNameTableNameSet = new HashSet<String>();
        for (DfTableMetaInfo info : tableMetaInfoList) {
            final String tableName = info.getTableName();
            if (tableNameSet.contains(tableName)) {
                sameNameTableNameSet.add(tableName);
            }
            tableNameSet.add(tableName);
        }
        if (tableNameSet.size() == tableMetaInfoList.size()) {
            return;
        }
        for (DfTableMetaInfo tableMetaInfo : tableMetaInfoList) {
            final String tableName = tableMetaInfo.getTableName();
            if (sameNameTableNameSet.contains(tableName)) {
                _log.info("$ sameNameTable --> " + tableMetaInfo);
                tableMetaInfo.setExistSameNameTable(true);
            }
        }
    }

    /**
     * Get database-type-string-array.
     * 
     * @return Database-type-string-array. (NotNull)
     */
    protected String[] getDatabaseTypeStringArray() {
        final List<String> databaseTypeList = getProperties().getBasicProperties().getDatabaseTypeList();
        return databaseTypeList.toArray(new String[databaseTypeList.size()]);
    }

    /**
     * Log database-types. {This is a mere helper method.}
     * 
     * @param types Database-types. (NotNull)
     */
    protected void logDatabaseTypes(String[] types) {
        String typeString = "";
        for (int i = 0; i < types.length; i++) {
            if (i == 0) {
                typeString = types[i];
            } else {
                typeString = typeString + " - " + types[i];
            }
        }
        _log.info("$ DatabaseTypes are '" + typeString + "'");
    }

    /**
     * Is the database Oracle?
     * 
     * @return Determination.
     */
    protected boolean isOracle() {
        return DfBuildProperties.getInstance().getBasicProperties().isDatabaseOracle();
    }

    public static class DfTableMetaInfo {

        protected String _tableName;

        protected String _tableType;

        protected String _tableSchema;

        protected String _tableComment;

        protected boolean _existSameNameTable;

        public boolean isTableTypeView() {
            return _tableType != null ? _tableType.equalsIgnoreCase("VIEW") : false;
        }

        public String selectRealSchemaName(String schemaName) {
            if (isExistSameNameTable()) {
                return _tableSchema;
            } else {
                return schemaName;
            }
        }

        public String buildTableNameWithSchema() {
            if (_tableSchema != null && _tableSchema.trim().length() != 0) {
                return _tableSchema + "." + _tableName;
            } else {
                return _tableName;
            }
        }

        public String getTableName() {
            return _tableName;
        }

        public void setTableName(String tableName) {
            this._tableName = tableName;
        }

        public String getTableType() {
            return _tableType;
        }

        public void setTableType(String tableType) {
            this._tableType = tableType;
        }

        public String getTableSchema() {
            return _tableSchema;
        }

        public void setTableSchema(String tableSchema) {
            this._tableSchema = tableSchema;
        }

        public String getTableComment() {
            return _tableComment;
        }

        public void setTableComment(String tableComment) {
            this._tableComment = tableComment;
        }

        public boolean isExistSameNameTable() {
            return _existSameNameTable;
        }

        public void setExistSameNameTable(boolean existSameNameTable) {
            this._existSameNameTable = existSameNameTable;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof DfTableMetaInfo) {
                return getTableName().equals(((DfTableMetaInfo) obj).getTableName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return getTableName().hashCode();
        }

        @Override
        public String toString() {
            if (_tableSchema != null && _tableSchema.trim().length() != 0) {
                return _tableSchema + "." + _tableName + "(" + _tableType + "): " + _tableComment;
            } else {
                return _tableName + "(" + _tableType + "): " + _tableComment;
            }
        }
    }
}