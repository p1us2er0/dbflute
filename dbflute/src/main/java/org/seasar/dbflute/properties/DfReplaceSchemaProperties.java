package org.seasar.dbflute.properties;

import java.io.File;
import java.io.FileFilter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.helper.process.SystemScript;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.DfUrlAnalyzer;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.factory.DfUrlAnalyzerFactory;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfLoadedDataInfo;
import org.seasar.dbflute.properties.assistant.DfConnectionProperties;
import org.seasar.dbflute.properties.assistant.DfReplaceSchemaResourceFinder;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public final class DfReplaceSchemaProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfReplaceSchemaProperties.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfReplaceSchemaProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                          replaceSchemaDefinitionMap
    //                                                          ==========================
    public static final String KEY_replaceSchemaDefinitionMap = "replaceSchemaDefinitionMap";
    protected Map<String, Object> _replaceSchemaDefinitionMap;

    public Map<String, Object> getReplaceSchemaDefinitionMap() {
        if (_replaceSchemaDefinitionMap == null) {
            _replaceSchemaDefinitionMap = mapProp("torque." + KEY_replaceSchemaDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _replaceSchemaDefinitionMap;
    }

    // ===================================================================================
    //                                                                      Base Directory
    //                                                                      ==============
    public String getPlaySqlDir() {
        return getPlaySqlDirPureName(); // path relative to DBFlute client
    }

    public String getPlaySqlDirPureName() {
        return "playsql";
    }

    public String getPlaySqlDirSymbol() {
        return getPlaySqlDirPureName();
    }

    // ===================================================================================
    //                                                                          Schema SQL
    //                                                                          ==========
    // -----------------------------------------------------
    //                                         Create Schema
    //                                         -------------
    public String getReplaceSchemaSqlTitle() {
        return "replace-schema";
    }

    public List<File> getReplaceSchemaSqlFileList(String sqlRootDir) {
        return doGetSchemaSqlFileList(sqlRootDir, getReplaceSchemaSqlTitle());
    }

    public Map<String, File> getReplaceSchemaSqlFileMap(String sqlRootDir) {
        return doGetSchemaSqlFileMap(getReplaceSchemaSqlFileList(sqlRootDir));
    }

    public Map<String, File> doGetSchemaSqlFileMap(List<File> sqlFileList) {
        final Map<String, File> resultMap = new LinkedHashMap<String, File>();
        for (File sqlFile : sqlFileList) {
            // Schema SQL files are located in the same directory
            final String uniqueKey = sqlFile.getName();
            resultMap.put(uniqueKey, sqlFile);
        }
        return resultMap;
    }

    protected List<File> doGetSchemaSqlFileList(String targetDir, String title) {
        return doGetResourceFileList(targetDir, title, ".sql");
    }

    protected List<File> doGetResourceFileList(String targetDir, String prefix, String... suffixes) {
        final DfReplaceSchemaResourceFinder finder = new DfReplaceSchemaResourceFinder();
        finder.addPrefix(prefix);
        for (String suffix : suffixes) {
            finder.addSuffix(suffix);
        }
        return finder.findResourceFileList(targetDir);
    }

    // -----------------------------------------------------
    //                                          Take Finally
    //                                          ------------
    protected String getTakeFinallySqlFile() {
        return getPlaySqlDir() + "/take-finally.sql";
    }

    public String getTakeFinallySqlTitle() {
        return "take-finally";
    }

    public List<File> getTakeFinallySqlFileList(String sqlRootDir) {
        return doGetSchemaSqlFileList(sqlRootDir, getTakeFinallySqlTitle());
    }

    public Map<String, File> getTakeFinallySqlFileMap(String sqlRootDir) {
        return doGetSchemaSqlFileMap(getTakeFinallySqlFileList(sqlRootDir));
    }

    // ===================================================================================
    //                                                                         Schema Data
    //                                                                         ===========
    public String getSchemaDataDir(String baseDir) {
        return baseDir + "/data";
    }

    public String getCommonDataDir(String baseDir, String typeName) {
        return getSchemaDataDir(baseDir) + "/common/" + typeName;
    }

    public String getLoadTypeDataDir(String baseDir, String loadType, String typeName) {
        return getSchemaDataDir(baseDir) + "/" + loadType + "/" + typeName;
    }

    protected List<File> doGetCommonDataFileList(String baseDir, String typeName) { // contains data-prop
        return doGetAnyTypeDataFileList(baseDir, DfLoadedDataInfo.COMMON_LOAD_TYPE, typeName);
    }

    protected List<File> doGetLoadTypeDataFileList(String baseDir, String typeName) { // contains data-prop
        return doGetAnyTypeDataFileList(baseDir, getDataLoadingType(), typeName);
    }

    protected List<File> doGetAnyTypeDataFileList(String baseDir, String loadType, String typeName) { // contains data-prop
        final String targetDir = getLoadTypeDataDir(baseDir, loadType, typeName);
        final String firstxlsFileType = DfLoadedDataInfo.FIRSTXLS_FILE_TYPE;
        final String xlsFileType = DfLoadedDataInfo.XLS_FILE_TYPE;
        final String suffix = "." + (typeName.equals(firstxlsFileType) ? xlsFileType : typeName);
        if (Srl.equalsPlain(typeName, firstxlsFileType, xlsFileType)) {
            return doGetDataFileList(targetDir, suffix, false);
        } else { // delimiter data (contains one level nested)
            return doGetDataFileList(targetDir, suffix, true);
        }
    }

    protected List<File> doGetDataFileList(String targetDir, String suffix, boolean oneLevelNested) {
        final DfReplaceSchemaResourceFinder finder = new DfReplaceSchemaResourceFinder();
        finder.addSuffix(suffix);
        finder.addSuffix(".dataprop"); // contains data-prop
        if (oneLevelNested) {
            finder.containsOneLevelNested();
        }
        return finder.findResourceFileList(targetDir);
    }

    // non-ApplicationPlaySql below

    protected String getMainCurrentLoadTypeDataDir() {
        final String playSqlDirectory = getPlaySqlDir();
        final String dataLoadingType = getDataLoadingType();
        return playSqlDirectory + "/data/" + dataLoadingType;
    }

    public String getMainCurrentLoadTypeDataDir(String fileType) {
        return getMainCurrentLoadTypeDataDir() + "/" + fileType;
    }

    public String getMainCurrentLoadTypeFirstXlsDataDir() {
        return getMainCurrentLoadTypeDataDir() + "/firstxls";
    }

    public String getMainCurrentLoadTypeTsvDataDir() {
        return getMainCurrentLoadTypeDataDir() + "/tsv";
    }

    public String getMainCurrentLoadTypeTsvUTF8DataDir() {
        return getMainCurrentLoadTypeTsvDataDir() + "/UTF-8";
    }

    public String getMainCurrentLoadTypeCsvDataDir() {
        return getMainCurrentLoadTypeDataDir() + "/csv";
    }

    public String getMainCurrentLoadTypeCsvUTF8DataDir() {
        return getMainCurrentLoadTypeCsvDataDir() + "/csv/UTF-8";
    }

    public String getMainCurrentLoadTypeXlsDataDir() {
        return getMainCurrentLoadTypeDataDir() + "/xls";
    }

    public List<File> findSchemaDataAllList(String sqlRootDir) { // contains data-prop
        final File sqlRoot = new File(sqlRootDir);
        final List<File> fileList = new ArrayList<File>();
        doFindHierarchyFileList(fileList, sqlRoot);
        return fileList;
    }

    protected void doFindHierarchyFileList(final List<File> fileList, File baseDir) {
        if (baseDir.getName().startsWith(".")) { // closed directory
            return; // e.g. .svn
        }
        final File[] listFiles = baseDir.listFiles(new FileFilter() {
            public boolean accept(File subFile) {
                if (subFile.isDirectory()) {
                    doFindHierarchyFileList(fileList, subFile);
                    return false;
                }
                return true;
            }
        });
        if (listFiles != null) {
            fileList.addAll(Arrays.asList(listFiles));
        }
    }

    // ===================================================================================
    //                                                                   Data Loading Type
    //                                                                   =================
    public String getDataLoadingType() {
        final String propString = (String) getReplaceSchemaDefinitionMap().get("dataLoadingType");
        if (propString == null) {
            return getOldStyleEnvironmentType();
        }
        return propString;
    }

    protected String getOldStyleEnvironmentType() { // Old Style!
        final String propString = (String) getReplaceSchemaDefinitionMap().get("environmentType");
        if (propString == null) {
            return "ut";
        }
        return propString;
    }

    // ===================================================================================
    //                                                                Filter Variables Map
    //                                                                ====================
    protected Map<String, String> _filterVariablesMap;

    @SuppressWarnings("unchecked")
    protected Map<String, String> getFilterVariablesMap() {
        if (_filterVariablesMap != null) {
            return _filterVariablesMap;
        }
        _filterVariablesMap = (Map<String, String>) getReplaceSchemaDefinitionMap().get("filterVariablesMap");
        if (_filterVariablesMap == null) {
            _filterVariablesMap = new LinkedHashMap<String, String>();
        }
        setupDefaultFilterVariables(_filterVariablesMap);
        return _filterVariablesMap;
    }

    protected void setupDefaultFilterVariables(Map<String, String> filterVariablesMap) {
        final DfDatabaseProperties prop = getDatabaseProperties();
        filterVariablesMap.put("dfprop.mainCatalog", prop.getDatabaseCatalog());
        filterVariablesMap.put("dfprop.mainSchema", prop.getDatabaseSchema().getPureSchema());
        filterVariablesMap.put("dfprop.mainUser", prop.getDatabaseUser());
        filterVariablesMap.put("dfprop.mainPassword", prop.getDatabasePassword());
    }

    protected String getFilterVariablesBeginMark() {
        return "/*$";
    }

    protected String getFilterVariablesEndMark() {
        return "*/";
    }

    public String resolveFilterVariablesIfNeeds(String sql) {
        final String beginMark = getFilterVariablesBeginMark();
        final String endMark = getFilterVariablesEndMark();
        final Map<String, String> filterVariablesMap = getFilterVariablesMap();
        if (!filterVariablesMap.isEmpty() && sql.contains(beginMark) && sql.contains(endMark)) {
            final Set<Entry<String, String>> entrySet = filterVariablesMap.entrySet();
            for (Entry<String, String> entry : entrySet) {
                final String variableMark = beginMark + entry.getKey() + endMark;
                if (sql.contains(variableMark)) {
                    sql = replaceString(sql, variableMark, entry.getValue());
                }
            }
        }
        return sql;
    }

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
    public boolean isLoggingInsertSql() {
        return isProperty("isLoggingInsertSql", true, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                                   SQL File Encoding
    //                                                                   =================
    public String getSqlFileEncoding() {
        final String sqlFileEncoding = (String) getReplaceSchemaDefinitionMap().get("sqlFileEncoding");
        if (sqlFileEncoding != null && sqlFileEncoding.trim().length() != 0) {
            return sqlFileEncoding;
        } else {
            return "UTF-8";
        }
    }

    // ===================================================================================
    //                                                                          Skip Sheet
    //                                                                          ==========
    public String getSkipSheet() {
        final String skipSheet = (String) getReplaceSchemaDefinitionMap().get("skipSheet");
        if (skipSheet != null && skipSheet.trim().length() != 0) {
            return skipSheet;
        } else {
            return null;
        }
    }

    // ===================================================================================
    //                                                                  Increment Sequence
    //                                                                  ==================
    public boolean isIncrementSequenceToDataMax() {
        return isProperty("isIncrementSequenceToDataMax", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                               Suppress Batch Update
    //                                                               =====================
    public boolean isSuppressBatchUpdate() {
        return isProperty("isSuppressBatchUpdate", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                             Object Type Target List
    //                                                             =======================
    protected List<String> _objectTypeTargetList;

    public List<String> getObjectTypeTargetList() { // overrides the property of databaseInfoMap 
        final Object obj = getReplaceSchemaDefinitionMap().get("objectTypeTargetList");
        if (obj != null && !(obj instanceof List<?>)) {
            String msg = "The type of the property 'objectTypeTargetList' should be List: " + obj;
            throw new DfIllegalPropertyTypeException(msg);
        }
        final List<String> defaultObjectTypeTargetList = getDefaultObjectTypeTargetList();
        if (obj == null) {
            _objectTypeTargetList = defaultObjectTypeTargetList;
        } else {
            @SuppressWarnings("unchecked")
            final List<String> list = (List<String>) obj;
            _objectTypeTargetList = !list.isEmpty() ? list : defaultObjectTypeTargetList;
        }
        return _objectTypeTargetList;
    }

    protected List<String> getDefaultObjectTypeTargetList() {
        return getDatabaseProperties().getObjectTypeTargetList(); // inherit
    }

    // ===================================================================================
    //                                                                     Additional User
    //                                                                     ===============
    protected Map<String, Map<String, String>> _additionalUesrMap;

    public Map<String, Map<String, String>> getAdditionalUserMap() {
        if (_additionalUesrMap != null) {
            return _additionalUesrMap;
        }
        final Object obj = getReplaceSchemaDefinitionMap().get("additionalUserMap");
        if (obj != null && !(obj instanceof Map<?, ?>)) {
            String msg = "The type of the property 'additionalUserMap' should be Map: " + obj;
            throw new DfIllegalPropertyTypeException(msg);
        }
        if (obj == null) {
            _additionalUesrMap = DfCollectionUtil.emptyMap();
        } else {
            @SuppressWarnings("unchecked")
            final Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) obj;
            _additionalUesrMap = map;
        }
        return _additionalUesrMap;
    }

    protected Map<String, String> getAdditionalUserPropertyMap(String additonalUser) {
        return getAdditionalUserMap().get(additonalUser);
    }

    public Connection createAdditionalUserConnection(String additonalUser) {
        final Map<String, String> propertyMap = getAdditionalUserPropertyMap(additonalUser);
        if (propertyMap == null) {
            return null;
        }
        final String driver = getDatabaseProperties().getDatabaseDriver();
        final String url;
        {
            String property = propertyMap.get("url");
            if (property != null && property.trim().length() > 0) {
                url = property;
            } else {
                url = getDatabaseProperties().getDatabaseUrl();
            }
        }
        final DfUrlAnalyzerFactory factory = new DfUrlAnalyzerFactory(getBasicProperties(), url);
        final DfUrlAnalyzer analyzer = factory.createAnalyzer();
        final String catalog = analyzer.extractCatalog();
        final String schema = propertyMap.get("schema");
        final UnifiedSchema unifiedSchema = UnifiedSchema.createAsDynamicSchema(catalog, schema);
        final String user = propertyMap.get("user");
        final String password = propertyMap.get("password");
        _log.info("...Creating a connection for additional user");
        return createConnection(driver, url, unifiedSchema, user, password);
    }

    // ===================================================================================
    //                                                                     Additional Drop
    //                                                                     ===============
    protected List<Map<String, Object>> _additionalDropMapList;

    public List<Map<String, Object>> getAdditionalDropMapList() {
        if (_additionalDropMapList != null) {
            return _additionalDropMapList;
        }
        final Object obj = getReplaceSchemaDefinitionMap().get("additionalDropMapList");
        if (obj == null) {
            _additionalDropMapList = DfCollectionUtil.emptyList();
        } else {
            _additionalDropMapList = castToList(obj, "additionalDropMapList");
        }
        return _additionalDropMapList;
    }

    public String getAdditionalDropUrl(Map<String, Object> additionalDropMap) {
        final Object obj = additionalDropMap.get("url");
        if (obj == null) {
            return null;
        }
        return castToString(obj, "additionalDropMapList.url");
    }

    public String getAdditionalDropUser(Map<String, Object> additionalDropMap) {
        final Object obj = additionalDropMap.get("user");
        if (obj == null) {
            return null;
        }
        return castToString(obj, "additionalDropMapList.user");
    }

    public String getAdditionalDropPassword(Map<String, Object> additionalDropMap) {
        final Object obj = additionalDropMap.get("password");
        if (obj == null) {
            return null;
        }
        return castToString(obj, "additionalDropMapList.password");
    }

    @SuppressWarnings("unchecked")
    public Properties getAdditionalDropPropertiesMap(Map<String, Object> additionalDropMap) {
        Object obj = additionalDropMap.get("propertiesMap");
        if (obj == null) {
            return new Properties();
        }
        if (!(obj instanceof Map)) {
            String msg = "The schema should be Map<String, String>:";
            msg = msg + " propertiesMap=" + obj + " type=" + obj.getClass();
            throw new DfIllegalPropertyTypeException(msg);
        }
        final Properties prop = new Properties();
        prop.putAll((Map<String, String>) obj);
        return prop;
    }

    public UnifiedSchema getAdditionalDropSchema(Map<String, Object> additionalDropMap) {
        final String url = getAdditionalDropUrl(additionalDropMap);
        final String catalog;
        if (Srl.is_NotNull_and_NotTrimmedEmpty(url)) {
            final DfUrlAnalyzerFactory factory = new DfUrlAnalyzerFactory(getBasicProperties(), url);
            final DfUrlAnalyzer analyzer = factory.createAnalyzer();
            catalog = analyzer.extractCatalog();
        } else {
            catalog = getDatabaseProperties().getDatabaseCatalog();
        }
        final Object obj = additionalDropMap.get("schema");
        if (obj == null) {
            if (!isDatabaseAsSchemaSpecificationOmittable()) {
                String msg = "The schema is required:";
                msg = msg + " additionalDropMap=" + additionalDropMap;
                throw new DfRequiredPropertyNotFoundException(msg);
            }
            return null;
        }
        final String schema = castToString(obj, "additionalDropMapList.schema");
        final UnifiedSchema unifiedSchema = UnifiedSchema.createAsDynamicSchema(catalog, schema);
        return unifiedSchema;
    }

    protected boolean isDatabaseAsSchemaSpecificationOmittable() {
        return getBasicProperties().isDatabaseAsSchemaSpecificationOmittable();
    }

    public List<String> getAdditionalDropObjectTypeList(Map<String, Object> additionalDropMap) {
        Object obj = additionalDropMap.get("objectTypeTargetList");
        if (obj == null) {
            obj = additionalDropMap.get("objectTypeList"); // old style
            if (obj == null) {
                final List<String> defaultList = new ArrayList<String>();
                defaultList.add(DfConnectionProperties.OBJECT_TYPE_TABLE);
                defaultList.add(DfConnectionProperties.OBJECT_TYPE_VIEW);
                return defaultList;
            }
        }
        return castToList(obj, "additionalDropMapList.objectTypeTargetList");
    }

    public Connection createAdditionalDropConnection(Map<String, Object> additionalDropMap) {
        final String driver = getDatabaseProperties().getDatabaseDriver();
        String url = getAdditionalDropUrl(additionalDropMap);
        url = url != null && url.trim().length() > 0 ? url : getDatabaseProperties().getDatabaseUrl();
        String user = getAdditionalDropUser(additionalDropMap);
        String password;
        if (user != null && user.trim().length() > 0) {
            password = getAdditionalDropPassword(additionalDropMap);
            if (password == null || password.trim().length() == 0) {
                String msg = "The password is required when the user is specified:";
                msg = msg + " user=" + user + " additionalDropMap=" + additionalDropMap;
                throw new DfIllegalPropertySettingException(msg);
            }
        } else {
            user = getDatabaseProperties().getDatabaseUser();
            password = getDatabaseProperties().getDatabasePassword();
        }
        final Properties prop = getAdditionalDropPropertiesMap(additionalDropMap);
        Properties info = new Properties();
        info.putAll(prop);
        info.put("user", user);
        info.put("password", password);
        _log.info("...Creating a connection for additional drop");
        return createConnection(driver, url, getAdditionalDropSchema(additionalDropMap), info);
    }

    // ===================================================================================
    //                                                                 Application PlaySql
    //                                                                 ===================
    public String getApplicationPlaySqlDirectory() {
        return getProperty("applicationPlaySqlDirectory", null, getReplaceSchemaDefinitionMap());
    }

    public List<File> getApplicationReplaceSchemaSqlFileList() {
        final String targetDir = getApplicationPlaySqlDirectory();
        if (targetDir == null) {
            return DfCollectionUtil.emptyList();
        }
        return doGetSchemaSqlFileList(targetDir, getReplaceSchemaSqlTitle());
    }

    public List<File> getAppcalitionTakeFinallySqlFileList() {
        final String targetDir = getApplicationPlaySqlDirectory();
        if (targetDir == null) {
            return DfCollectionUtil.emptyList();
        }
        return doGetSchemaSqlFileList(targetDir, getTakeFinallySqlTitle());
    }

    // ===================================================================================
    //                                                        Suppress Initializing Schema
    //                                                        ============================
    public boolean isSuppressTruncateTable() {
        return isProperty("isSuppressTruncateTable", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropForeignKey() {
        return isProperty("isSuppressDropForeignKey", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropTable() {
        return isProperty("isSuppressDropTable", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropSequence() {
        return isProperty("isSuppressDropSequence", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropProcedure() {
        return isProperty("isSuppressDropProcedure", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropDBLink() {
        return isProperty("isSuppressDropDBLink", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                                           Migration
    //                                                                           =========
    // -----------------------------------------------------
    //                                        Basic Resource
    //                                        --------------
    protected String getMigrationDir() {
        final String playSqlDirectory = getPlaySqlDir();
        return playSqlDirectory + "/" + getMigrationDirPureName();
    }

    protected String getMigrationDirPureName() {
        return "migration";
    }

    // -----------------------------------------------------
    //                                        Alter Resource
    //                                        --------------
    public String getMigrationAlterDirectory() {
        return getMigrationDir() + "/alter";
    }

    protected List<File> _migrationAlterSqlFileList;

    public List<File> getMigrationAlterSqlFileList() { // contains script files
        if (_migrationAlterSqlFileList != null) {
            return _migrationAlterSqlFileList;
        }
        final String targetDir = getMigrationAlterDirectory();
        final String sqlTitle = getAlterSchemaSqlTitle();
        final List<String> suffixList = new ArrayList<String>();
        suffixList.add(".sql");
        suffixList.addAll(SystemScript.getSupportedExtList());
        _migrationAlterSqlFileList = doGetResourceFileList(targetDir, sqlTitle, suffixList.toArray(new String[] {}));
        return _migrationAlterSqlFileList;
    }

    public String getAlterSchemaSqlTitle() {
        return "alter-schema";
    }

    public boolean hasMigrationAlterSqlResource() {
        return !getMigrationAlterSqlFileList().isEmpty();
    }

    // -----------------------------------------------------
    //                                     Previous Resource
    //                                     -----------------
    public String getMigrationPreviousDir() {
        return getMigrationDir() + "/previous";
    }

    public boolean hasMigrationPreviousResource() {
        return !getMigrationPreviousReplaceSchemaSqlFileList().isEmpty();
    }

    protected List<File> _migrationPreviousReplaceSchemaSqlFileList;

    public List<File> getMigrationPreviousReplaceSchemaSqlFileList() {
        if (_migrationPreviousReplaceSchemaSqlFileList != null) {
            return _migrationPreviousReplaceSchemaSqlFileList;
        }
        final String targetDir = getMigrationPreviousDir();
        final String sqlTitle = getReplaceSchemaSqlTitle();
        _migrationPreviousReplaceSchemaSqlFileList = doGetSchemaSqlFileList(targetDir, sqlTitle);
        return _migrationPreviousReplaceSchemaSqlFileList;
    }

    public Map<String, File> getMigrationPreviousReplaceSchemaSqlFileMap() {
        return doGetSchemaSqlFileMap(getMigrationPreviousReplaceSchemaSqlFileList());
    }

    protected List<File> _migrationPreviousTakeFinallySqlFileList;

    public List<File> getMigrationPreviousTakeFinallySqlFileList() {
        if (_migrationPreviousTakeFinallySqlFileList != null) {
            return _migrationPreviousTakeFinallySqlFileList;
        }
        final String targetDir = getMigrationPreviousDir();
        final String sqlTitle = getTakeFinallySqlTitle();
        _migrationPreviousTakeFinallySqlFileList = doGetSchemaSqlFileList(targetDir, sqlTitle);
        return _migrationPreviousTakeFinallySqlFileList;
    }

    public Map<String, File> getMigrationPreviousTakeFinallySqlFileMap() {
        return doGetSchemaSqlFileMap(getMigrationPreviousTakeFinallySqlFileList());
    }

    // -----------------------------------------------------
    //                                      History Resource
    //                                      ----------------
    public String getMigrationHistoryDir() {
        final String baseDirectory = getMigrationDir();
        return baseDirectory + "/history";
    }

    // -----------------------------------------------------
    //                                       Schema Resource
    //                                       ---------------
    public String getMigrationAlterCheckSchemaXml() {
        final String baseDirectory = getMigrationDir();
        return baseDirectory + "/schema/migration-schema.xml";
    }

    public String getMigrationAlterCheckResultDiff() {
        final String baseDirectory = getMigrationDir();
        return baseDirectory + "/schema/alter-check-result.diffmap";
    }

    public String getMigrationChangeOutputResultDiff() {
        final String baseDirectory = getMigrationDir();
        return baseDirectory + "/schema/change-output-result.diffmap";
    }

    // -----------------------------------------------------
    //                                         Mark Resource
    //                                         -------------
    public String getMigrationSavePreviousMark() {
        return doGetMigrationMark("save-previous.dfmark");
    }

    public boolean hasMigrationSavePreviousMark() {
        return doHasMigrationMark(getMigrationSavePreviousMark());
    }

    public String getMigrationPreviousOKMark() {
        return doGetMigrationMark("previous-OK.dfmark");
    }

    public boolean hasMigrationPreviousOKMark() {
        return doHasMigrationMark(getMigrationPreviousOKMark());
    }

    public String getMigrationNextNGMark() {
        return doGetMigrationMark("next-NG.dfmark");
    }

    public boolean hasMigrationNextNGMark() {
        return doHasMigrationMark(getMigrationNextNGMark());
    }

    public String getMigrationAlterNGMark() {
        return doGetMigrationMark("alter-NG.dfmark");
    }

    public boolean hasMigrationAlterNGMark() {
        return doHasMigrationMark(getMigrationAlterNGMark());
    }

    public String getMigrationPreviousNGMark() {
        return doGetMigrationMark("previous-NG.dfmark");
    }

    public boolean hasMigrationPreviousNGMark() {
        return doHasMigrationMark(getMigrationPreviousNGMark());
    }

    protected String doGetMigrationMark(String pureName) {
        return getMigrationDir() + "/" + pureName;
    }

    protected boolean doHasMigrationMark(String markPath) {
        return new File(markPath).exists();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}