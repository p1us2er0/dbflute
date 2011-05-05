package org.seasar.dbflute.task;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfCreateSchemaFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyFailureException;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAbstractSchemaTaskFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAlterSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfCreateSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfLoadDataFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfReplaceSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfTakeFinallyFinalInfo;
import org.seasar.dbflute.logic.replaceschema.process.DfAlterCheckProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfAlterCheckProcess.CoreProcessPlayer;
import org.seasar.dbflute.logic.replaceschema.process.DfCreateSchemaProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfCreateSchemaProcess.CreatingDataSourcePlayer;
import org.seasar.dbflute.logic.replaceschema.process.DfLoadDataProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfTakeFinallyProcess;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfReplaceSchemaTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfReplaceSchemaTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _lazyConnection = false;
    protected DfReplaceSchemaFinalInfo _replaceSchemaFinalInfo;
    protected DfReplaceSchemaFinalInfo _rollbackSchemaFinalInfo;
    protected DfCreateSchemaFinalInfo _createSchemaFinalInfo;
    protected DfLoadDataFinalInfo _loadDataFinalInfo;
    protected DfTakeFinallyFinalInfo _takeFinallyFinalInfo;
    protected DfAlterSchemaFinalInfo _alterSchemaFinalInfo;

    // ===================================================================================
    //                                                                          DataSource
    //                                                                          ==========
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                         Change User
    //                                                                         ===========
    @Override
    protected void setupDataSource() throws SQLException {
        try {
            super.setupDataSource();
            getDataSource().getConnection(); // check
        } catch (SQLException e) {
            setupLazyConnection(e);
        }
    }

    protected void setupLazyConnection(SQLException e) throws SQLException {
        if (_lazyConnection) { // already lazy
            throw e;
        }
        String msg = e.getMessage();
        if (msg.length() > 50) {
            msg = msg.substring(0, 47) + "...";
        }
        _log.info("...Being a lazy connection: " + msg);
        destroyDataSource();
        _lazyConnection = true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        if (isChangeOutput()) {
            processChangeOutput();
        } else if (isAlterCheck()) {
            processAlterCheck();
        } else {
            processMain();
        }
    }

    protected boolean isChangeOutput() {
        if (hasPreviousNGMark()) {
            _log.info("*Found previous-NG mark, which supresses ChangeOutput process");
            return false;
        } else {
            return hasChangeOutputMark();
        }
    }

    protected boolean isAlterCheck() {
        if (hasPreviousNGMark()) {
            _log.info("*Found previous-NG mark, which supresses AlterCheck process");
            return false;
        } else {
            return hasAlterSqlResource();
        }
    }

    protected void processMain() {
        executeCoreProcess();
        closePreviousNGIfExists(); // after core process without error
    }

    protected void closePreviousNGIfExists() {
        if (hasPreviousNGMark()) {
            final String ngMark = getPreviousNGMark();
            final File previousNGMark = new File(ngMark);
            if (previousNGMark.exists()) {
                _log.info("...Deleting previous-NG mark: " + ngMark);
                previousNGMark.delete();
                refreshResources();
            }
        }
    }

    protected void processAlterCheck() {
        doProcessAlterCheck(false);
    }

    protected void processChangeOutput() {
        doProcessAlterCheck(true);
    }

    protected void doProcessAlterCheck(boolean changeOutput) {
        final DfAlterCheckProcess process = createAlterCheckProcess();
        try {
            if (changeOutput) {
                _alterSchemaFinalInfo = process.outputChange();
            } else {
                _alterSchemaFinalInfo = process.execute();
            }
            _alterSchemaFinalInfo.throwAlterCheckExceptionIfExists();
        } finally {
            // because the alter check process
            // may output alter NG mark file
            refreshResources();
        }
    }

    protected DfAlterCheckProcess createAlterCheckProcess() {
        return DfAlterCheckProcess.createAsMain(getDataSource(), new CoreProcessPlayer() {
            public void play() {
                executeCoreProcess();
            }

            public void rollback() {
                executeRollbackProcess();
            }
        });
    }

    // ===================================================================================
    //                                                                        Core Process
    //                                                                        ============
    protected void executeCoreProcess() {
        doExecuteCoreProcess(false);
    }

    protected void executeRollbackProcess() {
        doExecuteCoreProcess(true);
    }

    protected void doExecuteCoreProcess(boolean rollback) {
        try {
            createSchema();
            loadData();
            takeFinally();
        } finally {
            setupReplaceSchemaFinalInfo(rollback);
        }
        handleSchemaContinuedFailure(rollback);
    }

    protected void createSchema() {
        final DfCreateSchemaProcess process = DfCreateSchemaProcess.createAsCore(new CreatingDataSourcePlayer() {
            public DataSource callbackGetDataSource() {
                return getDataSource();
            }

            public void callbackSetupDataSource() throws SQLException {
                setupDataSource();
            }
        }, _lazyConnection);
        _createSchemaFinalInfo = process.execute();
    }

    protected void loadData() {
        final DfLoadDataProcess process = DfLoadDataProcess.createAsCore(getDataSource());
        _loadDataFinalInfo = process.execute();
        final RuntimeException loadEx = _loadDataFinalInfo.getLoadEx();
        if (loadEx != null) { // high priority exception
            throw loadEx;
        }
    }

    protected void takeFinally() {
        final DfTakeFinallyProcess process = DfTakeFinallyProcess.createAsCore(getDataSource());
        _takeFinallyFinalInfo = process.execute();
        final DfTakeFinallyAssertionFailureException assertionEx = _takeFinallyFinalInfo.getAssertionEx();
        if (assertionEx != null) { // high priority exception
            throw assertionEx;
        }
    }

    protected void setupReplaceSchemaFinalInfo(boolean rollback) {
        if (rollback) {
            _rollbackSchemaFinalInfo = createReplaceSchemaFinalInfo();
        } else { // main
            _replaceSchemaFinalInfo = createReplaceSchemaFinalInfo();
        }
    }

    protected DfReplaceSchemaFinalInfo createReplaceSchemaFinalInfo() {
        return new DfReplaceSchemaFinalInfo(_createSchemaFinalInfo, _loadDataFinalInfo, _takeFinallyFinalInfo);
    }

    protected void handleSchemaContinuedFailure(boolean rollback) { // means continued errors
        final DfReplaceSchemaFinalInfo finalInfo;
        if (rollback) {
            finalInfo = _rollbackSchemaFinalInfo;
        } else {
            finalInfo = _replaceSchemaFinalInfo;
        }
        if (finalInfo.isCreateSchemaFailure()) {
            String msg = "Failed to create schema (Look at the final info)";
            throw new DfCreateSchemaFailureException(msg);
        }
        if (finalInfo.isTakeFinallyFailure()) {
            String msg = "Failed to take finally (Look at the final info)";
            throw new DfTakeFinallyFailureException(msg);
        }
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    @Override
    public String getFinalInformation() {
        return buildReplaceSchemaFinalMessage();
    }

    protected String buildReplaceSchemaFinalMessage() {
        final DfReplaceSchemaFinalInfo finalInfo; // null allowed
        final boolean rollbackFailure;
        if (_rollbackSchemaFinalInfo != null && _rollbackSchemaFinalInfo.hasFailure()) {
            // failures of roll-back are prior here (means previous-NG exists)
            finalInfo = _rollbackSchemaFinalInfo;
            rollbackFailure = true;
        } else {
            finalInfo = _replaceSchemaFinalInfo;
            rollbackFailure = false;
        }
        final StringBuilder sb = new StringBuilder();
        boolean firstDone = false;

        // AlterSchema
        boolean alterFailure = false;
        {
            final DfAlterSchemaFinalInfo alterSchemaFinalInfo = _alterSchemaFinalInfo;
            if (alterSchemaFinalInfo != null && alterSchemaFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, alterSchemaFinalInfo);
                alterFailure = alterSchemaFinalInfo.isFailure();
            }
        }

        // CreateSchema
        if (finalInfo != null) {
            final DfCreateSchemaFinalInfo createSchemaFinalInfo = finalInfo.getCreateSchemaFinalInfo();
            if (createSchemaFinalInfo != null && createSchemaFinalInfo.isValidInfo()) {
                if (!alterFailure || createSchemaFinalInfo.isFailure()) {
                    if (firstDone) {
                        sb.append(ln()).append(ln());
                    }
                    firstDone = true;
                    buildSchemaTaskContents(sb, createSchemaFinalInfo);
                }
            }
        }

        // LoadData
        if (finalInfo != null) {
            final DfLoadDataFinalInfo loadDataFinalInfo = finalInfo.getLoadDataFinalInfo();
            if (loadDataFinalInfo != null && loadDataFinalInfo.isValidInfo()) {
                if (!alterFailure || loadDataFinalInfo.isFailure()) {
                    if (firstDone) {
                        sb.append(ln()).append(ln());
                    }
                    firstDone = true;
                    buildSchemaTaskContents(sb, loadDataFinalInfo);
                }
            }
        }

        // TakeFinally
        boolean assertionFailure = false;
        if (finalInfo != null) {
            final DfTakeFinallyFinalInfo takeFinallyFinalInfo = finalInfo.getTakeFinallyFinalInfo();
            if (takeFinallyFinalInfo != null) {
                assertionFailure = takeFinallyFinalInfo.hasAssertionFailure();
                if (takeFinallyFinalInfo.isValidInfo()) {
                    if (!alterFailure || takeFinallyFinalInfo.isFailure()) {
                        if (firstDone) {
                            sb.append(ln()).append(ln());
                        }
                        firstDone = true;
                        buildSchemaTaskContents(sb, takeFinallyFinalInfo);
                    }
                }
            }
        }

        if (rollbackFailure) { // roll-back in AlterCheck
            sb.append(ln()).append("    * * * * * * * * * * *");
            sb.append(ln()).append("    * Rollback Failure  *");
            sb.append(ln()).append("    * * * * * * * * * * *");
        } else if (alterFailure) { // alter or create in AlterCheck
            sb.append(ln()).append("    * * * * * * * * * * *");
            sb.append(ln()).append("    * Migration Failure *");
            sb.append(ln()).append("    * * * * * * * * * * *");
        } else if (assertionFailure) { // assertion in normal time
            sb.append(ln()).append("    * * * * * * * * * * *");
            sb.append(ln()).append("    * Assertion Failure *");
            sb.append(ln()).append("    * * * * * * * * * * *");
        } else if (finalInfo != null && finalInfo.hasFailure()) { // as default
            sb.append(ln()).append("    * * * * * *");
            sb.append(ln()).append("    * Failure *");
            sb.append(ln()).append("    * * * * * *");
        }
        return sb.toString();
    }

    protected void buildSchemaTaskContents(StringBuilder sb, DfAbstractSchemaTaskFinalInfo finalInfo) {
        sb.append(" ").append(finalInfo.getResultMessage());
        final List<String> detailMessageList = finalInfo.getDetailMessageList();
        for (String detailMessage : detailMessageList) {
            sb.append(ln()).append("  ").append(detailMessage);
        }
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return getProperties().getReplaceSchemaProperties();
    }

    public String getPreviousNGMark() {
        return getReplaceSchemaProperties().getMigrationPreviousNGMark();
    }

    public boolean hasPreviousNGMark() {
        return getReplaceSchemaProperties().hasMigrationPreviousNGMark();
    }

    public boolean hasChangeOutputMark() {
        return getReplaceSchemaProperties().hasMigrationChangeOutputMark();
    }

    public boolean hasAlterSqlResource() {
        return getReplaceSchemaProperties().hasMigrationAlterSqlResource();
    }
}