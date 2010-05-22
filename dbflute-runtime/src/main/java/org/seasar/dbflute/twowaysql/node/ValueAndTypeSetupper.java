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
package org.seasar.dbflute.twowaysql.node;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.exception.BindVariableCommentListIndexNotNumberException;
import org.seasar.dbflute.exception.BindVariableCommentNotFoundPropertyException;
import org.seasar.dbflute.exception.EmbeddedVariableCommentListIndexNotNumberException;
import org.seasar.dbflute.exception.EmbeddedVariableCommentNotFoundPropertyException;
import org.seasar.dbflute.exception.ForCommentListIndexNotNumberException;
import org.seasar.dbflute.exception.ForCommentNotFoundPropertyException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.twowaysql.pmbean.MapParameterBean;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ValueAndTypeSetupper {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String LIKE_SEARCH_OPTION_SUFFIX = "InternalLikeSearchOption";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<String> _nameList;
    protected String _expression; // for logging only
    protected String _specifiedSql; // for logging only
    protected CommentType _commentType; // for logging only

    public enum CommentType {
        BIND("bind variable comment", "Bind Variable Comment") // bind
        , EMBEDDED("embedded variable comment", "Embedded Variable Comment") // embedded
        , FORCOMMENT("FOR comment", "FOR COMMENT") // for comment
        ;
        private String _textName;
        private String _titleName;

        private CommentType(String commentName, String titleName) {
            _textName = commentName;
            _titleName = titleName;
        }

        public String textName() {
            return _textName;
        }

        public String titleName() {
            return _titleName;
        }
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param nameList The list of property names. (NotNull)
     * @param expression The expression of the comment for logging only. (NotNull)
     * @param specifiedSql The specified SQL for logging only. (NotNull)
     * @param commentType The type of comment for logging only. (NotNull)
     */
    public ValueAndTypeSetupper(List<String> nameList, String expression, String specifiedSql, CommentType commentType) {
        this._nameList = nameList;
        this._expression = expression;
        this._specifiedSql = specifiedSql;
        this._commentType = commentType;
    }

    // ===================================================================================
    //                                                                              Set up
    //                                                                              ======
    public void setupValueAndType(ValueAndType valueAndType) {
        Object value = valueAndType.getTargetValue();
        Class<?> clazz = valueAndType.getTargetType();

        // LikeSearchOption handling here is for OutsideSql.
        LikeSearchOption likeSearchOption = null;
        String rearOption = null;

        for (int pos = 1; pos < _nameList.size(); pos++) {
            if (value == null) {
                break;
            }
            final String currentName = _nameList.get(pos);
            if (pos == 1) { // at the first Loop
                final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(clazz);
                if (hasLikeSearchProperty(beanDesc, currentName, value)) {
                    likeSearchOption = getLikeSearchOption(beanDesc, currentName, value);
                }
            }
            if (List.class.isInstance(value) && currentName.startsWith("get(") && currentName.endsWith(")")) {
                // used when FOR comment
                final List<?> list = (List<?>) value;
                final String exp = Srl.extractFirstScope(currentName, "get(", ")");
                try {
                    final Integer index = DfTypeUtil.toInteger(exp);
                    value = list.get(index);
                    if (isLastLoopAndValidLikeSearch(pos, likeSearchOption, value)) {
                        value = likeSearchOption.generateRealValue((String) value);
                        rearOption = likeSearchOption.getRearOption();
                    }
                    clazz = (value != null ? value.getClass() : null);
                    continue;
                } catch (NumberFormatException e) {
                    throwListIndexNumberException(_expression, exp, _specifiedSql, _commentType, e);
                }
            }
            if (Map.class.isInstance(value)) { // used by union-query and so on...
                final Map<?, ?> map = (Map<?, ?>) value;
                value = map.get(currentName);
                if (isLastLoopAndValidLikeSearch(pos, likeSearchOption, value)) {
                    value = likeSearchOption.generateRealValue((String) value);
                    rearOption = likeSearchOption.getRearOption();
                }
                clazz = (value != null ? value.getClass() : null);
                continue;
            }
            final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(clazz);
            if (beanDesc.hasPropertyDesc(currentName)) { // main case
                final DfPropertyDesc pd = beanDesc.getPropertyDesc(currentName);
                value = getPropertyValue(clazz, value, currentName, pd);
                if (isLastLoopAndValidLikeSearch(pos, likeSearchOption, value)) {
                    value = likeSearchOption.generateRealValue((String) value);
                    rearOption = likeSearchOption.getRearOption();
                }
                clazz = (value != null ? value.getClass() : pd.getPropertyType());
                continue;
            }
            if (MapParameterBean.class.isAssignableFrom(clazz)) { // priority low
                final Map<String, Object> map = ((MapParameterBean) value).getParameterMap();
                if (map.containsKey(currentName)) { // if the property is defined
                    value = map.get(currentName);
                    if (isLastLoopAndValidLikeSearch(pos, likeSearchOption, value)) {
                        value = likeSearchOption.generateRealValue((String) value);
                        rearOption = likeSearchOption.getRearOption();
                    }
                    clazz = (value != null ? value.getClass() : null);
                    continue;
                }
            }
            throwNotFoundPropertyException(_expression, clazz, currentName, _specifiedSql, _commentType);
        }
        valueAndType.setTargetValue(value);
        valueAndType.setTargetType(clazz);
        valueAndType.setRearOption(rearOption);
    }

    // -----------------------------------------------------
    //                             LikeSearch for OutsideSql
    //                             -------------------------
    protected boolean hasLikeSearchProperty(DfBeanDesc beanDesc, String currentName, Object pmb) {
        final String propertyName = buildLikeSearchPropertyName(currentName);
        if (beanDesc.hasPropertyDesc(propertyName)) { // main case
            return true;
        }
        if (Map.class.isInstance(pmb)) {
            return ((Map<?, ?>) pmb).containsKey(propertyName);
        }
        if (MapParameterBean.class.isInstance(pmb)) {
            final Map<String, Object> map = ((MapParameterBean) pmb).getParameterMap();
            return map.containsKey(propertyName);
        }
        return false;
    }

    protected LikeSearchOption getLikeSearchOption(DfBeanDesc beanDesc, String currentName, Object pmb) {
        final String propertyName = buildLikeSearchPropertyName(currentName);
        final LikeSearchOption option;
        if (beanDesc.hasPropertyDesc(propertyName)) { // main case
            final DfPropertyDesc pb = beanDesc.getPropertyDesc(propertyName);
            option = (LikeSearchOption) pb.getValue(pmb);
        } else if (Map.class.isInstance(pmb)) {
            option = (LikeSearchOption) ((Map<?, ?>) pmb).get(propertyName);
        } else if (MapParameterBean.class.isInstance(pmb)) {
            final Map<String, Object> map = ((MapParameterBean) pmb).getParameterMap();
            option = (LikeSearchOption) map.get(propertyName);
        } else { // no way
            String msg = "Not found the like search property: name=" + propertyName;
            throw new IllegalStateException(msg);
        }
        // no check here for various situation
        return option;
    }

    protected String buildLikeSearchPropertyName(String resourceName) {
        return resourceName + LIKE_SEARCH_OPTION_SUFFIX;
    }

    protected boolean isLastLoopAndValidLikeSearch(int pos, LikeSearchOption option, Object value) {
        return isLastLoop(pos) && option != null && value != null && value instanceof String;
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected boolean isLastLoop(int pos) {
        return _nameList.size() == (pos + 1);
    }

    protected Object getPropertyValue(Class<?> beanType, Object beanValue, String currentName, DfPropertyDesc pd) {
        return pd.getValue(beanValue);
    }

    protected Object invokeGetter(Method method, Object target) {
        return DfReflectionUtil.invoke(method, target, null);
    }

    protected void throwNotFoundPropertyException(String expression, Class<?> targetType, String notFoundProperty,
            String specifiedSql, CommentType commentType) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The property on the " + commentType.textName() + " was not found!");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of your property on your arguments.");
        br.addElement("And has the property had misspelling?");
        br.addItem(commentType.titleName());
        br.addElement(expression);
        br.addItem("NotFound Property");
        br.addElement((targetType != null ? targetType.getName() + "#" : "") + notFoundProperty);
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (CommentType.BIND.equals(commentType)) {
            throw new BindVariableCommentNotFoundPropertyException(msg);
        } else if (CommentType.EMBEDDED.equals(commentType)) {
            throw new EmbeddedVariableCommentNotFoundPropertyException(msg);
        } else if (CommentType.FORCOMMENT.equals(commentType)) {
            throw new ForCommentNotFoundPropertyException(msg);
        } else { // no way
            throw new BindVariableCommentNotFoundPropertyException(msg);
        }
    }

    protected void throwListIndexNumberException(String expression, String notNumberIndex, String specifiedSql,
            CommentType commentType, NumberFormatException e) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The list index on the " + commentType.textName() + " was not number!");
        br.addItem("Advice");
        br.addElement("Please confirm the index on your comment.");
        br.addItem(commentType.titleName());
        br.addElement(expression);
        br.addItem("NotNumber Index");
        br.addElement(notNumberIndex);
        br.addItem("NumberFormatException");
        br.addElement(e.getMessage());
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (CommentType.BIND.equals(commentType)) {
            throw new BindVariableCommentListIndexNotNumberException(msg, e);
        } else if (CommentType.EMBEDDED.equals(commentType)) {
            throw new EmbeddedVariableCommentListIndexNotNumberException(msg, e);
        } else if (CommentType.FORCOMMENT.equals(commentType)) {
            throw new ForCommentListIndexNotNumberException(msg, e);
        } else { // no way
            throw new BindVariableCommentListIndexNotNumberException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(String name) {
        return Srl.initCap(name);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
