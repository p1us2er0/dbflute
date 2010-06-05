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

import java.lang.reflect.Array;
import java.util.List;

import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 */
public class EmbeddedVariableNode extends AbstractNode implements LoopAcceptable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX = "$";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected String _testValue;
    protected List<String> _nameList;
    protected String _specifiedSql;
    protected boolean _blockNullParameter; // for dynamic binding

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EmbeddedVariableNode(String expression, String testValue, String specifiedSql, boolean blockNullParameter) {
        this._expression = expression;
        this._testValue = testValue;
        this._nameList = Srl.splitList(expression, ".");
        this._specifiedSql = specifiedSql;
        this._blockNullParameter = blockNullParameter;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        final String firstName = _nameList.get(0);
        assertFirstNameAsNormal(ctx, firstName);
        final Object firstValue = ctx.getArg(firstName);
        final Class<?> firstType = ctx.getArgType(firstName);
        doAccept(ctx, firstValue, firstType);
    }

    public void accept(CommandContext ctx, LoopInfo loopInfo) { // for FOR comment
        final String firstName = _nameList.get(0);
        if (firstName.equals(ForNode.CURRENT_VARIABLE)) { // use loop element
            final Object parameter = loopInfo.getCurrentParameter();
            final Class<?> parameterType = loopInfo.getCurrentParameterType();
            doAccept(ctx, parameter, parameterType);
        } else { // normal
            accept(ctx);
        }
    }

    // *like-search option is unsupported in embedded comment

    protected void doAccept(CommandContext ctx, Object firstValue, Class<?> firstType) {
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(firstValue);
        valueAndType.setTargetType(firstType);
        setupValueAndType(valueAndType);

        final Object finalValue = valueAndType.getTargetValue();
        final Class<?> finalType = valueAndType.getTargetType();
        if (finalValue == null) {
            if (_blockNullParameter) {
                throwBindOrEmbeddedParameterNullValueException(valueAndType);
            }
            return;
        }
        if (isInScope()) {
            if (List.class.isAssignableFrom(finalType)) {
                embedArray(ctx, ((List<?>) finalValue).toArray());
            } else if (finalType.isArray()) {
                embedArray(ctx, finalValue);
            } else {
                throwBindOrEmbeddedCommentInScopeNotListException(valueAndType);
            }
        } else {
            final String embeddedString = finalValue.toString();
            if (embeddedString.indexOf("?") > -1) {
                String msg = "The value of expression for embedded comment should not contain a question mark '?':";
                msg = msg + " value=" + valueAndType.getTargetValue() + " expression=" + _expression;
                throw new IllegalStateException(msg);
            }
            if (isQuote()) {
                ctx.addSql("'" + embeddedString + "'");
                return;
            }
            if (processDynamicBinding(ctx, firstValue, firstType, embeddedString)) {
                return;
            }
            ctx.addSql(embeddedString);
        }
    }

    protected void assertFirstNameAsNormal(CommandContext ctx, String firstName) {
        if (NodeUtil.isCurrentVariableOutOfScope(firstName, false)) {
            throwLoopCurrentVariableOutOfForCommentException();
        }
        if (NodeUtil.isWrongParameterBeanName(firstName, ctx)) {
            throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException();
        }
    }

    protected void throwLoopCurrentVariableOutOfForCommentException() {
        NodeUtil.throwLoopCurrentVariableOutOfForCommentException(_expression, _specifiedSql);
    }

    protected void setupValueAndType(ValueAndType valueAndType) {
        final CommentType type = CommentType.EMBEDDED;
        final ValueAndTypeSetupper setupper = new ValueAndTypeSetupper(_nameList, _expression, _specifiedSql, type);
        setupper.setupValueAndType(valueAndType);
    }

    protected boolean processDynamicBinding(CommandContext ctx, Object firstValue, Class<?> firstType,
            String embeddedString) {
        final ScopeInfo first = Srl.extractScopeFirst(embeddedString, "/*", "*/");
        if (first == null) {
            return false;
        }
        final SqlAnalyzer analyzer = new SqlAnalyzer(embeddedString, _blockNullParameter);
        final Node rootNode = analyzer.analyze();
        final CommandContextCreator creator = new CommandContextCreator(new String[] { "pmb" },
                new Class<?>[] { firstType });
        final CommandContext rootCtx = creator.createCommandContext(new Object[] { firstValue });
        rootNode.accept(rootCtx);
        final String sql = rootCtx.getSql();
        ctx.addSql(sql, rootCtx.getBindVariables(), rootCtx.getBindVariableTypes());
        return true;
    }

    protected boolean isInScope() {
        if (_testValue == null) {
            return false;
        }
        return _testValue.startsWith("(") && _testValue.endsWith(")");
    }

    protected boolean isQuote() {
        if (_testValue == null) {
            return false;
        }
        return Srl.count(_testValue, "'") > 1 && _testValue.startsWith("'") && _testValue.endsWith("'");
    }

    protected void embedArray(CommandContext ctx, Object array) {
        if (array == null) {
            return;
        }
        final int length = Array.getLength(array);
        if (length == 0) {
            throwBindOrEmbeddedCommentParameterEmptyListException();
        }
        String quote = null;
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                quote = !(currentElement instanceof Number) ? "'" : "";
                break;
            }
        }
        if (quote == null) {
            throwBindOrEmbeddedCommentParameterNullOnlyListException();
        }
        boolean existsValidElements = false;
        ctx.addSql("(");
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                if (!existsValidElements) {
                    ctx.addSql(quote + currentElement + quote);
                    existsValidElements = true;
                } else {
                    ctx.addSql(", " + quote + currentElement + quote);
                }
            }
        }
        ctx.addSql(")");
    }

    // ===================================================================================
    //                                                                           Exception
    //                                                                           =========
    protected void throwBindOrEmbeddedParameterNullValueException(ValueAndType valueAndType) {
        final Class<?> targetType = valueAndType.getTargetType();
        NodeUtil.throwBindOrEmbeddedCommentParameterNullValueException(_expression, targetType, _specifiedSql, false);
    }

    protected void throwBindOrEmbeddedCommentInScopeNotListException(ValueAndType valueAndType) {
        final Class<?> targetType = valueAndType.getTargetType();
        NodeUtil.throwBindOrEmbeddedCommentInScopeNotListException(_expression, targetType, _specifiedSql, false);
    }

    protected void throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException() {
        NodeUtil
                .throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(_expression, _specifiedSql, false);
    }

    protected void throwBindOrEmbeddedCommentParameterEmptyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterEmptyListException(_expression, _specifiedSql, false);
    }

    protected void throwBindOrEmbeddedCommentParameterNullOnlyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterNullOnlyListException(_expression, _specifiedSql, false);
    }
}
