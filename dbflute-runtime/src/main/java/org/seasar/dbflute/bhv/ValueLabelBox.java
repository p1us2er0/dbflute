package org.seasar.dbflute.bhv;

/**
 * The class of Value-Label Box.
 * @author jflute
 * @deprecated Sorry! This class will be deleted at the future.
 */
public class ValueLabelBox {

    protected Object _value;

    protected String _label;

    public void setValueLabel(Object value, String label) {
        this._value = value;
        this._label = label;
    }

    public Object getValue() {
        return _value;
    }

    public String getLabel() {
        return _label;
    }
}
