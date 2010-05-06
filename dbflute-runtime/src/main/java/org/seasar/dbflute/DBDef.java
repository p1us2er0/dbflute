/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute;

import java.util.HashMap;
import java.util.Map;

import org.seasar.dbflute.dbway.DBWay;
import org.seasar.dbflute.dbway.WayOfDB2;
import org.seasar.dbflute.dbway.WayOfDerby;
import org.seasar.dbflute.dbway.WayOfFirebird;
import org.seasar.dbflute.dbway.WayOfH2;
import org.seasar.dbflute.dbway.WayOfMSAccess;
import org.seasar.dbflute.dbway.WayOfMySQL;
import org.seasar.dbflute.dbway.WayOfOracle;
import org.seasar.dbflute.dbway.WayOfPostgreSQL;
import org.seasar.dbflute.dbway.WayOfSQLServer;
import org.seasar.dbflute.dbway.WayOfSQLite;
import org.seasar.dbflute.dbway.WayOfUnknown;

/**
 * The definition of database.
 * @author jflute
 */
public enum DBDef {

    // ===================================================================================
    //                                                                                ENUM
    //                                                                                ====
    MySQL("mysql", null, new WayOfMySQL()) // supported
    , PostgreSQL("postgresql", "postgre", new WayOfPostgreSQL()) // supported
    , Oracle("oracle", null, new WayOfOracle()) // supported
    , DB2("db2", null, new WayOfDB2()) // supported
    , SQLServer("sqlserver", "mssql", new WayOfSQLServer()) // supported
    , FireBird("firebird", null, new WayOfFirebird()) // unsupported
    , H2("h2", null, new WayOfH2()) // supported
    , Derby("derby", null, new WayOfDerby()) // supported
    , SQLite("sqlite", null, new WayOfSQLite()) // semi-supported
    , MSAccess("msaccess", null, new WayOfMSAccess()) // semi-supported
    , Unknown("unknown", null, new WayOfUnknown());

    // ===================================================================================
    //                                                                    Static Reference
    //                                                                    ================
    // -----------------------------------------------------
    //                                            Code Value
    //                                            ----------
    private static final Map<String, DBDef> _codeValueMap = new HashMap<String, DBDef>();
    static {
        for (DBDef value : values()) {
            _codeValueMap.put(value.code().toLowerCase(), value);
        }
    }
    private static final Map<String, DBDef> _codeAliasValueMap = new HashMap<String, DBDef>();
    static {
        for (DBDef value : values()) {
            if (value.codeAlias() != null) {
                _codeAliasValueMap.put(value.codeAlias().toLowerCase(), value);
            }
        }
    }

    /**
     * @param code The code of the DB. (Nullable: If the code is null, it returns null)
     * @return The instance that has the code. (Nullable)
     */
    public static DBDef codeOf(String code) {
        if (code == null) {
            return null;
        }
        final String lowerCaseCode = code.toLowerCase();
        DBDef def = _codeValueMap.get(lowerCaseCode);
        if (def == null) {
            def = _codeAliasValueMap.get(lowerCaseCode);
        }
        return def;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The code of the DB. (NotNull) */
    private String _code;

    /** The code alias of the DB. (Nullable) */
    private String _codeAlias;

    /** The way of the DB. (NotNull) */
    private DBWay _dbway;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param code The code of the DB. (NotNull)
     * @param codeAlias The code alias of the DB. (Nullable)
     */
    private DBDef(String code, String codeAlias, DBWay dbway) {
        _code = code;
        _codeAlias = codeAlias;
        _dbway = dbway;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * @return The code of the DB. (NotNull)
     */
    public String code() {
        return _code;
    }

    /**
     * @return The code alias of the DB. (Nullable)
     */
    private String codeAlias() {
        return _codeAlias;
    }

    /**
     * @return The way of the DB. (NotNull)
     */
    public DBWay dbway() {
        return _dbway;
    }
}
