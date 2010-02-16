package org.seasar.dbflute.helper.dataset.states;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public interface DfDtsRowStates {

    DfDtsRowState UNCHANGED = new DfDtsUnchangedState();

    DfDtsRowState CREATED = new DfDtsCreatedState();

    DfDtsRowState MODIFIED = new DfDtsModifiedState();

    DfDtsRowState REMOVED = new DfDtsRemovedState();
}
