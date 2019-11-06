package dodona.reflection;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.WildcardType;
import java.lang.reflect.WildcardType;
import java.lang.reflect.TypeVariable;
import java.util.stream.IntStream;
import java.util.Optional;

public class Assignable {

    public static boolean check(Type a, Type b) {
        return !whynot(a, b).isPresent();
    }

    public static Optional<String> whynot(Type a, Type b) {
        Optional<String> reason = failComp("either type is unimplemented: %s = %s", a, b);
        try{ reason = specific((GenericArrayType)  a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specific((GenericArrayType)  a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specific((GenericArrayType)  a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specific((GenericArrayType)  a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specific((GenericArrayType)  a, (Class)             b); } catch(ClassCastException e) {}
        try{ reason = specific((ParameterizedType) a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specific((ParameterizedType) a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specific((ParameterizedType) a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specific((ParameterizedType) a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specific((ParameterizedType) a, (Class)             b); } catch(ClassCastException e) {}
        try{ reason = specific((TypeVariable)      a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specific((TypeVariable)      a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specific((TypeVariable)      a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specific((TypeVariable)      a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specific((TypeVariable)      a, (Class)             b); } catch(ClassCastException e) {}
        try{ reason = specific((WildcardType)      a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specific((WildcardType)      a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specific((WildcardType)      a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specific((WildcardType)      a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specific((WildcardType)      a, (Class)             b); } catch(ClassCastException e) {}
        try{ reason = specific((Class)             a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specific((Class)             a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specific((Class)             a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specific((Class)             a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specific((Class)             a, (Class)             b); } catch(ClassCastException e) {}
        return reason;
    }

    private static Optional<String> failComp(String format, Object a, Object b) {
        return Optional.of(String.format(format, a, b));
    }

    private static Optional<String> specific(GenericArrayType a, GenericArrayType b) {
        return whynot(a.getGenericComponentType(), b.getGenericComponentType());
    }

    private static Optional<String> specific(GenericArrayType a, ParameterizedType b) {
        return Optional.of("not assignable: GenericArrayType = ParameterizedType");
    }

    private static Optional<String> specific(GenericArrayType a, TypeVariable<?> b) {
        return Optional.of("not yet implemented: GenericArrayType = TypeVariable<?>");
    }

    private static Optional<String> specific(GenericArrayType a, WildcardType b) {
        return Optional.of("not yet implemented: GenericArrayType = WildcardType");
    }

    private static Optional<String> specific(GenericArrayType a, Class<?> b) {
        return Optional.of("not assignable: GenericArrayType = Class<?>");
    }

    private static Optional<String> specific(ParameterizedType a, GenericArrayType b) {
        return Optional.of("not assignable: ParameterizedType = GenericArrayType");
    }

    private static Optional<String> specific(ParameterizedType a, ParameterizedType b) {
        Optional<String> base = whynot(a.getRawType(), b.getRawType());
        if(base.isPresent()) return base;
        if(a.getActualTypeArguments().length != b.getActualTypeArguments().length) return Optional.of("Huh");
        for(int i = 0; i < a.getActualTypeArguments().length; i++) {
            if(!a.getActualTypeArguments()[i].equals(b.getActualTypeArguments()[i])) {
                return failComp(
                    "un=qual argument types: %s and %s.",
                    a.getActualTypeArguments()[i],
                    b.getActualTypeArguments()[i]
                );
            }
        }
        return Optional.empty();
    }

    private static Optional<String> specific(ParameterizedType a, TypeVariable<?> b) {
        return Optional.of("not yet implemented: ParameterizedType = TypeVariable<?>");
    }

    private static Optional<String> specific(ParameterizedType a, WildcardType b) {
        return Optional.of("not yet implemented: ParameterizedType = WildcardType");
    }

    private static Optional<String> specific(ParameterizedType a, Class<?> b) {
        return Optional.of("not assignable: ParameterizedType = Class<?>");
    }

    private static Optional<String> specific(TypeVariable<?> a, GenericArrayType b) {
        return Optional.of("not yet implemented: TypeVariable<?> = GenericArrayType");
    }

    private static Optional<String> specific(TypeVariable<?> a, ParameterizedType b) {
        return Optional.of("not yet implemented: TypeVariable<?> = ParameterizedType");
    }

    private static Optional<String> specific(TypeVariable<?> a, TypeVariable<?> b) {
        if(a.equals(b)) return Optional.empty();
        return failComp("different typevariables: %s and %s.", a, b);
    }

    private static Optional<String> specific(TypeVariable<?> a, WildcardType b) {
        return Optional.of("not yet implemented: TypeVariable<?> = WildcardType");
    }

    private static Optional<String> specific(TypeVariable<?> a, Class<?> b) {
        return Optional.of("not yet implemented: TypeVariable<?> = Class<?>");
    }

    private static Optional<String> specific(WildcardType a, GenericArrayType b) {
        return Optional.of("not yet implemented: WildcardType = GenericArrayType");
    }

    private static Optional<String> specific(WildcardType a, ParameterizedType b) {
        return Optional.of("not yet implemented: WildcardType = ParameterizedType");
    }

    private static Optional<String> specific(WildcardType a, TypeVariable<?> b) {
        return Optional.of("not yet implemented: WildcardType = TypeVariable<?>");
    }

    private static Optional<String> specific(WildcardType a, WildcardType b) {
        return Optional.of("not yet implemented: WildcardType = WildcardType");
    }

    private static Optional<String> specific(WildcardType a, Class<?> b) {
        return Optional.of("not yet implemented: WildcardType = Class<?>");
    }

    private static Optional<String> specific(Class<?> a, GenericArrayType b) {
        return Optional.of("not assignable: Class<?> = GenericArrayType");
    }

    private static Optional<String> specific(Class<?> a, ParameterizedType b) {
        return whynot(a, b.getRawType());
    }

    private static Optional<String> specific(Class<?> a, TypeVariable<?> b) {
        return Optional.of("not yet implemented: Class<?> = TypeVariable<?>");
    }

    private static Optional<String> specific(Class<?> a, WildcardType b) {
        return Optional.of("not yet implemented: Class<?> = WildcardType");
    }

    private static Optional<String> specific(Class<?> a, Class<?> b) {
        if(a.isAssignableFrom(b)) {
            return Optional.empty();
        } else {
            return failComp("unassignables classes: %s and %s", a, b);
        }
    }

}
