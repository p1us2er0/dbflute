package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 */
public final class DfTransferEntityProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfTransferEntityProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> transferEntityDefinitionMap;
    protected Map<String, Object> getTransferEntityDefinitionMap() {
        if (transferEntityDefinitionMap == null) {
            transferEntityDefinitionMap = mapProp("torque.transferEntityDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return transferEntityDefinitionMap;
    }
    
    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasTransferEntityDefinition() {
        return !getTransferEntityDefinitionMap().isEmpty();
    }
    
    // ===================================================================================
    //                                                                     Detail Property
    //                                                                     ===============
    public String getOutputDirectory() {
        final String value = (String)getTransferEntityDefinitionMap().get("outputDirectory");
        if (value == null) {
            return getBasicProperties().getJavaDir();
        }
        // TODO: @jflute -- 調整をすること
        return getBasicProperties().getJavaDir() + "/" + value;
    }
    
    public String getBaseEntityPackage() {
        final String value = (String)getTransferEntityDefinitionMap().get("baseEntityPackage");
        
        // TODO: @jflute -- 必須チェック
        
        return value;
    }
    
    public String getExtendedEntityPackage() {
        final String value = (String)getTransferEntityDefinitionMap().get("extendedEntityPackage");
        
        // TODO: @jflute -- 必須チェック
        
        return value;
    }
    
    public boolean isIndependent() {
        final String value = (String)getTransferEntityDefinitionMap().get("independent");
        return value != null && value.trim().equalsIgnoreCase("true");
    }
}