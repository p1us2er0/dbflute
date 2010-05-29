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

import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class IfNode extends ScopeNode implements LoopAcceptable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX = "IF ";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected String _specifiedSql;
    protected ElseNode _elseNode;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public IfNode(String expression, String specifiedSql) {
        this._expression = expression;
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        doAcceptByEvaluator(ctx, null);
    }

    public void accept(CommandContext ctx, LoopInfo loopInfo) {
        doAcceptByEvaluator(ctx, loopInfo);
    }

    protected void doAcceptByEvaluator(CommandContext ctx, LoopInfo loopInfo) {
        final IfCommentEvaluator evaluator = createIfCommentEvaluator(ctx, loopInfo);
        final boolean result = evaluator.evaluate();
        if (result) {
            processAcceptingChildren(ctx, loopInfo);
            ctx.setEnabled(true);
        } else if (_elseNode != null) {
            if (loopInfo != null) {
                _elseNode.accept(ctx, loopInfo);
            } else {
                _elseNode.accept(ctx);
            }
        }
    }

    protected IfCommentEvaluator createIfCommentEvaluator(final CommandContext ctx, final LoopInfo loopInfo) {
        return new IfCommentEvaluator(new ParameterFinder() {
            public Object find(String name) {
                if (loopInfo != null && ForNode.CURRENT_PARAMETER.equals(name)) {
                    return loopInfo.getCurrentParameter();
                } else {
                    return ctx.getArg(name);
                }
            }
        }, _expression, _specifiedSql);
    }

    protected String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _expression + ", " + _elseNode + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getExpression() {
        return _expression;
    }

    public ElseNode getElseNode() {
        return _elseNode;
    }

    public void setElseNode(ElseNode elseNode) {
        this._elseNode = elseNode;
    }
}
