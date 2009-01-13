package org.dbflute;

import java.util.HashMap;
import java.util.Map;

import org.dbflute.dbway.DBWay;
import org.dbflute.dbway.WayOfDB2;
import org.dbflute.dbway.WayOfDerby;
import org.dbflute.dbway.WayOfFirebird;
import org.dbflute.dbway.WayOfH2;
import org.dbflute.dbway.WayOfMSAccess;
import org.dbflute.dbway.WayOfMySQL;
import org.dbflute.dbway.WayOfOracle;
import org.dbflute.dbway.WayOfPostgreSQL;
import org.dbflute.dbway.WayOfSQLServer;
import org.dbflute.dbway.WayOfUnknown;

/**
 * The definition of database.
 * @author DBFlute(AutoGenerator)
 */
public enum DBDef {

    // ===================================================================================
    //                                                                                ENUM
    //                                                                                ====
    MySQL("mysql", null, new WayOfMySQL())
    , PostgreSQL("postgresql", "postgre", new WayOfPostgreSQL())
    , Oracle("oracle", null, new WayOfOracle())
    , DB2("db2", null, new WayOfDB2())
    , SQLServer("sqlserver", "mssql", new WayOfSQLServer())
    , FireBird("firebird", null, new WayOfFirebird())
    , H2("h2", null, new WayOfH2())
    , Derby("derby", null, new WayOfDerby())
    , MSAccess("msaccess", null, new WayOfMSAccess())
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
