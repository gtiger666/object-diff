package xyz.arbres.objdiff.core.metamodel.type;



import xyz.arbres.objdiff.common.collections.EnumerableFunction;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.EnumerationAwareOwnerContext;
import xyz.arbres.objdiff.core.metamodel.object.OwnerContext;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author bartosz walacik
 */
public class ArrayType extends ContainerType {

    public ArrayType(Type baseJavaType, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, typeMapperLazy);
    }

    @Override
    public List<Type> getConcreteClassTypeArguments() {
        return (List) Lists.immutableListOf( getBaseJavaClass().getComponentType() );
    }

    @Override
    public Object map(Object sourceArray, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction, owner);

        Object targetArray = newArray(sourceArray, null, false);

        int len = Array.getLength(sourceArray);
        EnumerationAwareOwnerContext enumerationContext = new IndexableEnumerationOwnerContext(owner);
        for (int i=0; i<len; i++){
            Object sourceVal = Array.get(sourceArray,i);
            Array.set(targetArray, i, mapFunction.apply(sourceVal, enumerationContext));
        }
        return targetArray;
    }

    @Override
    public boolean isEmpty(Object array) {
        return array == null ||  Array.getLength(array) == 0;
    }

    @Override
    public Object map(Object sourceArray, Function mapFunction, boolean filterNulls) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction);

        Object targetArray = newArray(sourceArray, mapFunction, true);

        int len = Array.getLength(sourceArray);
        int t = 0;
        for (int i=0; i<len; i++) {
            Object sourceVal = Array.get(sourceArray,i);

            Object mappedVal = mapFunction.apply(sourceVal);
            if (mappedVal == null && filterNulls) continue;
            Array.set(targetArray, t++, mappedVal);
        }
        return targetArray;
    }

    @Override
    protected Stream<Object> items(Object source) {
        if (source == null || Array.getLength(source) == 0) {
            return Stream.empty();
        }

        return Arrays.asList((Object[])source).stream();
    }

    private Object newArray(Object sourceArray, Function mapFunction, boolean doSample) {
        int len = Array.getLength(sourceArray);
        if (len == 0) {
            return sourceArray;
        }

        if (getItemClass().isPrimitive()){
            return Array.newInstance(getItemClass(), len);
        }

        if (doSample) {
            Object sample = mapFunction.apply(Array.get(sourceArray, 0));
            if (getItemClass().isAssignableFrom(sample.getClass())) {
                return Array.newInstance(getItemClass(), len);
            }
        }

        return Array.newInstance(Object.class, len);
    }

    @Override
    public boolean equals(Object one, Object two) {
        return xyz.arbres.objdiff.common.collections.Arrays.equals(one , two);
    }

    @Override
    public Object empty() {
        return Collections.emptyList().toArray();
    }

    @Override
    public Class<?> getEnumerableInterface() {
        throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED);
    }
}
