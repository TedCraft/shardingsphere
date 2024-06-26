/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.expr.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.expr.spi.InlineExpressionParser;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;

import java.util.Properties;

/**
 * Inline expression parser factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InlineExpressionParserFactory {
    
    private static final String TYPE_NAME_BEGIN_SYMBOL = "<";
    
    private static final String TYPE_NAME_END_SYMBOL = ">";
    
    /**
     * Create new instance of inline expression parser by inlineExpression.
     * And for compatibility reasons, inlineExpression allows to be null.
     *
     * @param inlineExpression inline expression
     * @return created instance
     */
    public static InlineExpressionParser newInstance(final String inlineExpression) {
        Properties props = new Properties();
        if (null == inlineExpression) {
            return TypedSPILoader.getService(InlineExpressionParser.class, "GROOVY", props);
        }
        if (!inlineExpression.startsWith(TYPE_NAME_BEGIN_SYMBOL)) {
            props.setProperty(InlineExpressionParser.INLINE_EXPRESSION_KEY, inlineExpression);
            return TypedSPILoader.getService(InlineExpressionParser.class, "GROOVY", props);
        }
        Integer typeBeginIndex = inlineExpression.indexOf(TYPE_NAME_BEGIN_SYMBOL);
        Integer typeEndIndex = inlineExpression.indexOf(TYPE_NAME_END_SYMBOL);
        props.setProperty(InlineExpressionParser.INLINE_EXPRESSION_KEY, removeTypeNameInExpr(inlineExpression, typeBeginIndex, typeEndIndex));
        return TypedSPILoader.getService(InlineExpressionParser.class, getTypeName(inlineExpression, typeBeginIndex, typeEndIndex), props);
    }
    
    private static String getTypeName(final String inlineExpression, final Integer beginIndex, final Integer endIndex) {
        return beginIndex.equals(-1) || endIndex.equals(-1) ? "GROOVY" : inlineExpression.substring(beginIndex + 1, endIndex);
    }
    
    private static String removeTypeNameInExpr(final String inlineExpression, final Integer beginIndex, final Integer endIndex) {
        return inlineExpression.substring(0, beginIndex) + inlineExpression.substring(endIndex + 1);
    }
}
