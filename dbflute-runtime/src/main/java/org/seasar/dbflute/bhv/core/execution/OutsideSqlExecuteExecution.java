/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv.core.execution;

import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlFilter;
import org.seasar.dbflute.s2dao.sqlhandler.TnBasicParameterHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnBasicUpdateHandler;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class OutsideSqlExecuteExecution extends AbstractOutsideSqlExecution {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlExecuteExecution(DataSource dataSource, StatementFactory statementFactory, String twoWaySql,
            Map<String, Class<?>> argNameTypeMap) {
        super(dataSource, statementFactory, twoWaySql, argNameTypeMap);
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    @Override
    protected TnBasicParameterHandler newBasicParameterHandler(String executedSql) {
        return new TnBasicUpdateHandler(_dataSource, _statementFactory, executedSql);
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    @Override
    protected Object filterReturnValue(Object returnValue) {
        return DfTypeUtil.toInteger(returnValue); // just in case
    }

    @Override
    protected OutsideSqlFilter.ExecutionFilterType getOutsideSqlExecutionFilterType() {
        return OutsideSqlFilter.ExecutionFilterType.EXECUTE;
    }
}
