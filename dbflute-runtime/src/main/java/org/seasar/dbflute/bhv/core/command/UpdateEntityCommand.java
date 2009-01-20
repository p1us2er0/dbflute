package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlcommand.TnUpdateModifiedOnlyCommand;


/**
 * @author jflute
 */
public class UpdateEntityCommand extends AbstractEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "update";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createUpdateEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createUpdateEntitySqlExecution(TnBeanMetaData bmd) {
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createUpdateModifiedOnlyCommand(bmd, propertyNames);
    }

    protected TnUpdateModifiedOnlyCommand createUpdateModifiedOnlyCommand(TnBeanMetaData bmd, String[] propertyNames) {
        final TnUpdateModifiedOnlyCommand cmd = new TnUpdateModifiedOnlyCommand(_dataSource, _statementFactory);
        cmd.setBeanMetaData(bmd);// Extension Point!
        cmd.setPropertyNames(propertyNames);
        cmd.setOptimisticLockHandling(isOptimisticLockHandling());
        cmd.setVersionNoAutoIncrementOnMemory(isOptimisticLockHandling());
        return cmd;
    }
    
    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
