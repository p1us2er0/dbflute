package org.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.PropertyType;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.dbflute.s2dao.metadata.TnBeanMetaData;

/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalInsertAutoHandler extends InternalAbstractAutoHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalInsertAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            PropertyType[] propertyTypes) {
        super(dataSource, statementFactory, beanMetaData, propertyTypes);
        setOptimisticLockHandling(false);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
	@Override
    protected void setupBindVariables(Object bean) {
        setupInsertBindVariables(bean);
        setLoggingMessageSqlArgs(bindVariables);
    }

	@Override
    protected void preUpdateBean(Object bean) {
	    TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getIdentifierGeneratorSize(); i++) {
            TnIdentifierGenerator generator = bmd.getIdentifierGenerator(i);
            if (generator.isSelfGenerate()) {
                generator.setIdentifier(bean, getDataSource());
            }
        }
    }
	
	@Override
    protected void postUpdateBean(Object bean, int ret) {
	    TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getIdentifierGeneratorSize(); i++) {
            TnIdentifierGenerator generator = bmd.getIdentifierGenerator(i);
            if (!generator.isSelfGenerate()) {
                generator.setIdentifier(bean, getDataSource());
            }
        }
        updateVersionNoIfNeed(bean);
        updateTimestampIfNeed(bean);
    }
}
