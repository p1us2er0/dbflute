package org.seasar.dbflute.helper.stacktrace;

/**
 * @author jflute
 */
public interface InvokeNameExtractingResource {

    public boolean isTargetElement(String className, String methodName);
    public String filterSimpleClassName(String simpleClassName);
    public boolean isUseAdditionalInfo();
    public int getStartIndex();
    public int getLoopSize();
}