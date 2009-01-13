package org.dbflute.dbmeta;

/**
 * The interface of DB meta.
 * @author DBFlute(AutoGenerator)
 */
public interface DBMetaProvider {

    /**
     * Provide the DB meta.
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (Nullable: If the DB meta is not found, it returns null)
     */
    public DBMeta provideDBMeta(String tableFlexibleName);

    /**
     * Provide the DB meta.
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (NotNull)
     * @exception org.dbflute.exception.DBMetaNotFoundException When the DB meta is not found.
     */
    public DBMeta provideDBMetaChecked(String tableFlexibleName);
}
