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
package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.procedure.TnProcedureMetaData;
import org.seasar.dbflute.s2dao.procedure.TnProcedureMetaDataFactory;
import org.seasar.dbflute.s2dao.sqlcommand.TnProcedureCommand;

/**
 * The behavior command for OutsideSql.execute().
 * @author jflute
 */
public class OutsideSqlCallCommand extends AbstractOutsideSqlCommand<Void> {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "call";
    }

    public Class<?> getCommandReturnType() {
        return void.class;
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isProcedure() {
        return true;
    }

    public boolean isSelect() {
        return false;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution() {
        assertStatus("beforeGettingSqlExecution");
        final String path = _outsideSqlPath;
        final Object pmb = _parameterBean;
        final OutsideSqlOption option = _outsideSqlOption;
        final OutsideSqlContext outsideSqlContext = createOutsideSqlContext();
        outsideSqlContext.setDynamicBinding(option.isDynamicBinding());
        outsideSqlContext.setOffsetByCursorForcedly(option.isAutoPaging());
        outsideSqlContext.setLimitByCursorForcedly(option.isAutoPaging());
        outsideSqlContext.setOutsideSqlPath(path);
        outsideSqlContext.setParameterBean(pmb);
        outsideSqlContext.setMethodName(getCommandName());
        outsideSqlContext.setStatementConfig(option.getStatementConfig());
        outsideSqlContext.setTableDbName(option.getTableDbName());
		outsideSqlContext.setupBehaviorQueryPathIfNeeds();
        OutsideSqlContext.setOutsideSqlContextOnThread(outsideSqlContext);
    }

    public void afterExecuting() {
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        return generateSpecifiedOutsideSqlUniqueKey();
    }

    protected String generateSpecifiedOutsideSqlUniqueKey() {
        final String methodName = getCommandName();
        final String path = _outsideSqlPath;
        final Object pmb = _parameterBean;
        final OutsideSqlOption option = _outsideSqlOption;
        return OutsideSqlContext.generateSpecifiedOutsideSqlUniqueKey(methodName, path, pmb, option, null);
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
                return createOutsideSqlCallCommand(outsideSqlContext);
            }
        };
    }

    protected SqlExecution createOutsideSqlCallCommand(OutsideSqlContext outsideSqlContext) {
        // - - - - - - - - - - - - - - - - - - - - - - -
        // The attribute of Specified-OutsideSqlContext.
        // - - - - - - - - - - - - - - - - - - - - - - -
        final Object pmb = outsideSqlContext.getParameterBean();
        final String procedureName = outsideSqlContext.getOutsideSqlPath();

        // - - - - - - - - - - - - - - -
        // The attribute of SqlCommand.
        // - - - - - - - - - - - - - - -
        final TnProcedureMetaDataFactory factory = createProcedureMetaDataFactory();
        factory.setValueTypeFactory(_valueTypeFactory);
        final Class<?> pmbType = (pmb != null ? pmb.getClass() : null);
        final TnProcedureMetaData metaData = factory.createProcedureMetaData(procedureName, pmbType);
        return createProcedureCommand(metaData);
    }

    protected TnProcedureMetaDataFactory createProcedureMetaDataFactory() {
        return new TnProcedureMetaDataFactory();
    }

    protected TnProcedureCommand createProcedureCommand(TnProcedureMetaData metaData) {
        // Because a procedure command does not use result set handler.
        final TnResultSetHandler resultSetHandler = new NullResultSetHandler(); 
        return new TnProcedureCommand(_dataSource, resultSetHandler, _statementFactory, metaData);
    }

    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // InternalProcedureCommand switches argument so this is unnecessary actually!
    // - - - - - - - - - -/
    public Object[] getSqlExecutionArgument() {
        return new Object[] { _parameterBean };
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertStatus(String methodName) {
        assertBasicProperty(methodName);
        assertComponentProperty(methodName);
        assertOutsideSqlBasic(methodName);
        if (_parameterBean == null) {
            String msg = "The property 'parameterBean' should not be null";
            msg = msg + " when you call " + methodName + "().";
            throw new IllegalStateException(msg);
        }
    }
}
