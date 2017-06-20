package org.hyperfit.utils;

import org.hyperfit.exception.ParameterizedTypeException;
import org.javatuples.Pair;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class caches return type information
 */
public class TypeInfo {
    private final Map<String, Type> typeParamsLookup = new HashMap<String, Type>();

    private void add(String s, Type t) {
        this.typeParamsLookup.put(s, t);
    }

    private Type lookForTypeParam(TypeVariable arg) {
        Type type = this.typeParamsLookup.get(arg.getName());

        if(type == null) {
            throw new ParameterizedTypeException("No type param called [" + arg.getName() + "] was found in typeParamsLookup map");
        }

        return type;
    }

    public Pair<? extends Class<?>, Type> getArrayType(Class returnClass, Type genericReturnType, Type genericFallback) {

        Class<?> arrayComponentType = returnClass.getComponentType();
        Type genericComponentType = arrayComponentType;

        //If it's generic..override the type
        if (genericReturnType instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) genericReturnType;
            genericComponentType = arrayType.getGenericComponentType();

            //If the type is a variable, look it up
            if (genericComponentType instanceof TypeVariable) {
                genericComponentType = this.typeParamsLookup.get(((TypeVariable) genericComponentType).getName());
            }

            if(genericComponentType == null){
                //If we don't have the type info..then it's probably someone not using generics IE Page vs Page<T>, let's fallback
                genericComponentType = genericFallback;
            }

            arrayComponentType = (Class) genericComponentType;
        }

        return Pair.with(arrayComponentType, genericComponentType);
    }


    public TypeInfo make(Type resourceType) {
        TypeInfo typeInfo = new TypeInfo();

        if (resourceType != null) {
            if (resourceType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) resourceType;

                Type[] actualTypeArgs = paramType.getActualTypeArguments();
                TypeVariable[] params = ((Class) paramType.getRawType()).getTypeParameters();

                for (int i = 0; i < actualTypeArgs.length; i++) {

                    //If the actual type arg is a TypeVariable itself we need to look it up
                    //This happens when generics return generics, like Page<T>::next
                    Type arg = actualTypeArgs[i];
                    if (arg instanceof TypeVariable) {
                        TypeVariable tv = (TypeVariable) arg;
                        Type actualType = this.lookForTypeParam(tv);
                        typeInfo.add(tv.getName(), actualType);

                    } else {
                        typeInfo.add(params[i].getName(), arg);
                    }

                }
            }

        }

        return typeInfo;
    }
}
