package org.dbflute.bhv.core.command;

import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.bhv.core.SqlExecutionCreator;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionBeanContext;
import org.seasar.extension.jdbc.ResultSetHandler;


/**
 * @author DBFlute(AutoGenerator)
 */
public class SelectCountCBCommand extends AbstractSelectCBCommand<Integer> {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "selectCount";
    }

    public Class<?> getCommandReturnType() {
        return Integer.class;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution() {
        assertStatus("beforeGettingSqlExecution");
        final ConditionBean cb = _conditionBean;
        cb.xsetupSelectCountIgnoreFetchScope(); // *Point!
        ConditionBeanContext.setConditionBeanOnThread(cb);
    }

    public void afterExecuting() {
        assertStatus("afterExecuting");
        final ConditionBean cb = _conditionBean;
        cb.xafterCareSelectCountIgnoreFetchScope();
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isSelectCount() {
        return true;
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                ResultSetHandler handler = createObjectResultSetHandler(getCommandReturnType());
                return createSelectCBExecution(_conditionBeanType, handler);
            }
        };
    }
}
