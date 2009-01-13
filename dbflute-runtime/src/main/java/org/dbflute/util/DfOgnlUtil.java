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
package org.dbflute.util;

import java.util.Map;

import ognl.ClassResolver;
import ognl.Ognl;
import ognl.OgnlException;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.exception.OgnlRuntimeException;

/**
 * @author jflute
 */
public class DfOgnlUtil {

    protected DfOgnlUtil() {
    }

    public static Object getValue(Object exp, Object root) {
        return getValue(exp, root, null, 0);
    }

    public static Object getValue(Object exp, Object root, String path, int lineNumber) {
        return getValue(exp, null, root, path, lineNumber);
    }

    @SuppressWarnings("unchecked")
    public static Object getValue(Object exp, Map ctx, Object root) {
        return getValue(exp, ctx, root, null, 0);
    }

    @SuppressWarnings("unchecked")
    public static Object getValue(Object exp, Map ctx, Object root, String path, int lineNumber)
            throws OgnlRuntimeException {
        try {
            Map newCtx = addClassResolverIfNecessary(ctx, root);
            if (newCtx != null) {
                return Ognl.getValue(exp, newCtx, root);
            } else {
                return Ognl.getValue(exp, root);
            }
        } catch (OgnlException ex) {
            throw new OgnlRuntimeException(ex.getReason() == null ? ex : ex.getReason(), path, lineNumber);
        } catch (Exception ex) {
            throw new OgnlRuntimeException(ex, path, lineNumber);
        }
    }

    public static Object parseExpression(String expression) {
        return parseExpression(expression, null, 0);
    }

    public static Object parseExpression(String expression, String path, int lineNumber) throws OgnlRuntimeException {
        try {
            return Ognl.parseExpression(expression);
        } catch (Exception ex) {
            throw new OgnlRuntimeException(ex, path, lineNumber);
        }
    }

    @SuppressWarnings("unchecked")
    static Map addClassResolverIfNecessary(Map ctx, Object root) {
        if (root instanceof S2Container) {
            S2Container container = (S2Container) root;
            ClassLoader classLoader = container.getClassLoader();
            if (classLoader != null) {
                ClassResolverImpl classResolver = new ClassResolverImpl(classLoader);
                if (ctx == null) {
                    ctx = Ognl.createDefaultContext(root, classResolver);
                } else {
                    ctx = Ognl.addDefaultContext(root, classResolver, ctx);
                }
            }
        }
        return ctx;
    }

    public static class ClassResolverImpl implements ClassResolver {
        final private ClassLoader classLoader;

        public ClassResolverImpl(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @SuppressWarnings("unchecked")
        public Class<?> classForName(String className, Map ctx) throws ClassNotFoundException {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException ex) {
                int dot = className.indexOf('.');
                if (dot < 0) {
                    return classLoader.loadClass("java.lang." + className);
                } else {
                    throw ex;
                }
            }
        }
    }
}
