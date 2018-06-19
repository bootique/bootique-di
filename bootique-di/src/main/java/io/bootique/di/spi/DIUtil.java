/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.di.spi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.inject.Provider;

/**
 * A helper class used by Bootique DI implementation.
 */
class DIUtil {

    /**
     * @param provider to get name for
     * @return name of provider
     */
    static String getProviderName(Provider<?> provider) {
        if(provider instanceof NamedProvider) {
            return ((NamedProvider<?>) provider).getName();
        }
        return provider.getClass().getName();
    }

    static Type getGenericParameterType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] parameters = parameterizedType.getActualTypeArguments();

            if (parameters.length == 1) {
                return parameters[0];
            }
        }

        return null;
    }

    static Class<?> parameterClass(Type type) {
        Type parameterType = getGenericParameterType(type);
        if(parameterType == null) {
            return null;
        }

        return typeToClass(parameterType);
    }

    private static Class<?> typeToClass(Type type) {
        if(type instanceof Class) {
            return  (Class<?>) type;
        } else if(type instanceof ParameterizedType){
            return  (Class<?>) ((ParameterizedType)type).getRawType();
        } else {
            return Object.class;
        }
    }

}
