package dodona.reflection;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static Optional<String> failComp(String format, Object a, Object b) {
        return Optional.of(String.format(format, a, b));
    }

    private static Optional<String> notAssignable(Object a, Object b) {
        return failComp("not assignable: %s = %s", a, b);
    }

    private static Optional<String> onlyIfEqual(Type a, Type b) {
        if(a.equals(b)) return Optional.empty();
        return failComp("%s is not equal to %s", a, b);
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

    private static Optional<String> specific(GenericArrayType a, GenericArrayType b) {
        return whynot(a.getGenericComponentType(), b.getGenericComponentType());
    }

    private static Optional<String> specific(GenericArrayType a, ParameterizedType b) {
        return Optional.of("not assignable: GenericArrayType = ParameterizedType");
    }

    private static Optional<String> specific(GenericArrayType a, TypeVariable<?> b) {
        return Optional.of("a type variable cannot extend a GenericArrayType");
    }

    private static Optional<String> specific(GenericArrayType a, WildcardType b) {
        return Optional.of("a wildcard cannot extend a GenericArrayType");
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
            Optional<String> reason = erasableFrom(a.getActualTypeArguments()[i], b.getActualTypeArguments()[i]);
            if(reason.isPresent()) return reason;
        }
        return Optional.empty();
    }

    private static Optional<String> specific(ParameterizedType a, TypeVariable<?> b) {
        for(int i = 0; i < b.getBounds().length; i++) {
            Optional<String> reason = whynot(a, b.getBounds()[i]);
            if(reason.isPresent()) return reason;
        }
        return Optional.empty();
    }

    private static Optional<String> specific(ParameterizedType a, WildcardType b) {
        for(int i = 0; i < b.getUpperBounds().length; i++) {
            Optional<String> reason = whynot(a, b.getUpperBounds()[i]);
            if(reason.isPresent()) return reason;
        }
        if(b.getLowerBounds().length > 0) {
            return Optional.of("cannot assign a wildcard with lower bounds to a parameterized type");
        }
        return Optional.empty();
    }

    private static Optional<String> specific(ParameterizedType a, Class<?> b) {
        return Optional.of("not assignable: ParameterizedType = Class<?>");
    }

    private static Optional<String> specific(TypeVariable<?> a, GenericArrayType b) {
        if(a.getBounds().length == 1 && a.getBounds()[0] == Object.class) return Optional.empty();
        return Optional.of("cannot assign a generic array type to a type variable with bounds");
    }

    private static Optional<String> specific(TypeVariable<?> a, ParameterizedType b) {
        for(Type t : a.getBounds()) {
            Optional<String> reason = whynot(t, b);
            if(reason.isPresent()) return reason;
        }
        return Optional.empty();
    }

    private static Optional<String> specific(TypeVariable<?> a, TypeVariable<?> b) {
        for(Type t : a.getBounds()) {
            if(!Arrays.stream(b.getBounds()).anyMatch(t::equals)) {
                return failComp("not all bounds of %s were satisfied by %s", a, b);
            }
        }
        return Optional.empty();
    }

    private static Optional<String> specific(TypeVariable<?> a, WildcardType b) {
        for(Type t : a.getBounds()) {
            if(!Arrays.stream(b.getUpperBounds()).anyMatch(t::equals)) {
                return failComp("not all bounds of %s were satisfied by %s", a, b);
            }
        }
        return Optional.empty();
    }

    private static Optional<String> specific(TypeVariable<?> a, Class<?> b) {
        for(Type t : a.getBounds()) {
            Optional<String> reason = whynot(t, b);
            if(reason.isPresent()) return reason;
        }
        return Optional.empty();
    }

    private static Optional<String> specific(WildcardType a, GenericArrayType b) {
        return Optional.of("cannot write a wildcard type as argument type");
    }

    private static Optional<String> specific(WildcardType a, ParameterizedType b) {
        return Optional.of("cannot write a wildcard type as argument type");
    }

    private static Optional<String> specific(WildcardType a, TypeVariable<?> b) {
        return Optional.of("cannot write a wildcard type as argument type");
    }

    private static Optional<String> specific(WildcardType a, WildcardType b) {
        return Optional.of("cannot write a wildcard type as argument type");
    }

    private static Optional<String> specific(WildcardType a, Class<?> b) {
        return Optional.of("cannot write a wildcard type as argument type");
    }

    private static Optional<String> specific(Class<?> a, GenericArrayType b) {
        return Optional.of("not assignable: Class<?> = GenericArrayType");
    }

    private static Optional<String> specific(Class<?> a, ParameterizedType b) {
        return whynot(a, b.getRawType());
    }

    private static Optional<String> specific(Class<?> a, TypeVariable<?> b) {
        for(Type t : b.getBounds()) {
            Optional<String> reason = whynot(a, t);
            if(!reason.isPresent()) return Optional.empty();
        }
        return failComp("%s as no bound satisfying %s", b, a);
    }

    private static Optional<String> specific(Class<?> a, WildcardType b) {
        for(Type t : b.getUpperBounds()) {
            Optional<String> reason = whynot(a, t);
            if(!reason.isPresent()) return Optional.empty();
        }
        return failComp("%s as no upper bound satisfying %s", b, a);
    }

    private static Optional<String> specific(Class<?> a, Class<?> b) {
        if(a.isAssignableFrom(b)) {
            return Optional.empty();
        } else {
            return failComp("unassignables classes: %s and %s", a, b);
        }
    }

    private static Optional<String> erasableFrom(Type a, Type b) {
        Optional<String> reason = failComp("either type is unimplemented: %s = %s", a, b);
        try{ reason = specificErasableFrom((GenericArrayType)  a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((GenericArrayType)  a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((GenericArrayType)  a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((GenericArrayType)  a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((GenericArrayType)  a, (Class)             b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((ParameterizedType) a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((ParameterizedType) a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((ParameterizedType) a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((ParameterizedType) a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((ParameterizedType) a, (Class)             b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((TypeVariable)      a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((TypeVariable)      a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((TypeVariable)      a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((TypeVariable)      a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((TypeVariable)      a, (Class)             b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((WildcardType)      a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((WildcardType)      a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((WildcardType)      a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((WildcardType)      a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((WildcardType)      a, (Class)             b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((Class)             a, (GenericArrayType)  b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((Class)             a, (ParameterizedType) b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((Class)             a, (TypeVariable)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((Class)             a, (WildcardType)      b); } catch(ClassCastException e) {}
        try{ reason = specificErasableFrom((Class)             a, (Class)             b); } catch(ClassCastException e) {}
        return reason;
    }

    private static Optional<String> specificErasableFrom(GenericArrayType a, GenericArrayType b) {
        return onlyIfEqual(a, b);
    }

    private static Optional<String> specificErasableFrom(GenericArrayType a, ParameterizedType b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(GenericArrayType a, TypeVariable<?> b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(GenericArrayType a, WildcardType b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(GenericArrayType a, Class<?> b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(ParameterizedType a, GenericArrayType b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(ParameterizedType a, ParameterizedType b) {
        return onlyIfEqual(a, b);
    }

    private static Optional<String> specificErasableFrom(ParameterizedType a, TypeVariable<?> b) {
        for(int i = 0; i < b.getBounds().length; i++) {
            if(a.equals(b.getBounds()[i])) return Optional.empty();
        }
        return failComp("none of the bounds of %s equal to %s", b, a);
    }

    private static Optional<String> specificErasableFrom(ParameterizedType a, WildcardType b) {
        for(int i = 0; i < b.getUpperBounds().length; i++) {
            if(a.equals(b.getUpperBounds()[i])) return Optional.empty();
        }
        return failComp("none of the bounds of %s equal to %s", b, a);
    }

    private static Optional<String> specificErasableFrom(ParameterizedType a, Class<?> b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(TypeVariable<?> a, GenericArrayType b) {
        if(a.getBounds().length == 0) return Optional.empty(); // TODO always object
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(TypeVariable<?> a, ParameterizedType b) {
        if(a.getBounds().length == 0) return Optional.empty(); // TODO always object
        for(int i = 0; i < a.getBounds().length; i++) {
            Optional<String> reason = whynot(a.getBounds()[i], b);
            if(reason.isPresent()) return reason;
        }
        return Optional.empty();
    }

    private static Optional<String> specificErasableFrom(TypeVariable<?> a, TypeVariable<?> b) {
        for(int i = 0; i < a.getBounds().length; i++) {
            int j = 0;
            while(j < b.getBounds().length && !b.getBounds()[j].equals(a.getBounds()[i])) j++;
            if(j == b.getBounds().length) return failComp("not all bounds of %s were satisfied by %s", a, b);
        }
        return Optional.empty();
    }

    private static Optional<String> specificErasableFrom(TypeVariable<?> a, WildcardType b) {
        for(int i = 0; i < a.getBounds().length; i++) {
            int j = 0;
            while(j < b.getUpperBounds().length && !b.getUpperBounds()[j].equals(a.getBounds()[i])) j++;
            if(j == b.getUpperBounds().length) return failComp("not all bounds of %s were satisfied by %s", a, b);
        }
        return Optional.empty();
    }

    private static Optional<String> specificErasableFrom(TypeVariable<?> a, Class<?> b) {
        for(int i = 0; i < a.getBounds().length; i++) {
            Optional<String> reason = whynot(a.getBounds()[i], b);
            if(reason.isPresent()) return reason;
        }
        return Optional.empty();
    }

    private static Optional<String> specificErasableFrom(WildcardType a, GenericArrayType b) {
        if(a.getLowerBounds().length != 0) return Optional.of("a generic array type could never satisfy a lower bound");
        if(a.getUpperBounds().length == 1 && a.getUpperBounds()[0] == Object.class) return Optional.empty();
        return Optional.of("a generic array type could never satisfy an upper bound");
    }

    private static Optional<String> specificErasableFrom(WildcardType a, ParameterizedType b) {
        for(int i = 0; i < a.getLowerBounds().length; i++) {
            Optional<String> reason = whynot(b, a.getLowerBounds()[i]);
            if(reason.isPresent()) return reason;
        }
        for(int i = 0; i < a.getUpperBounds().length; i++) {
            Optional<String> reason = whynot(a.getUpperBounds()[i], b);
            if(reason.isPresent()) return reason;
        }
        return Optional.empty();
    }

    private static Optional<String> specificErasableFrom(WildcardType a, TypeVariable<?> b) {
        if(a.getLowerBounds().length != 0) return Optional.of("a type variable could never satisfy a lower bound");
        for(int i = 0; i < a.getUpperBounds().length; i++) {
            int j = 0;
            while(j < b.getBounds().length && !b.getBounds()[j].equals(a.getUpperBounds()[i])) j++;
            if(j == b.getBounds().length) return failComp("not all bounds of %s were satisfied by %s", a, b);
        }
        return Optional.empty();
    }

    private static Optional<String> specificErasableFrom(WildcardType a, WildcardType b) {
        for(int i = 0; i < a.getUpperBounds().length; i++) {
            int j = 0;
            while(j < b.getUpperBounds().length && !b.getUpperBounds()[j].equals(a.getUpperBounds()[i])) j++;
            if(j == b.getUpperBounds().length) return failComp("not all bounds of %s were satisfied by %s", a, b);
        }
        for(int i = 0; i < b.getLowerBounds().length; i++) {
            int j = 0;
            while(j < a.getLowerBounds().length && !a.getLowerBounds()[j].equals(b.getLowerBounds()[i])) j++;
            if(j == a.getLowerBounds().length) return failComp("not all bounds of %s were satisfied by %s", b, a);
        }
        return Optional.empty();
    }

    private static Optional<String> specificErasableFrom(WildcardType a, Class<?> b) {
        for(int i = 0; i < a.getLowerBounds().length; i++) {
            Optional<String> reason = whynot(b, a.getLowerBounds()[i]);
            if(reason.isPresent()) return reason;
        }
        for(int i = 0; i < a.getUpperBounds().length; i++) {
            Optional<String> reason = whynot(a.getUpperBounds()[i], b);
            if(reason.isPresent()) return reason;
        }
        return Optional.empty();
    }

    private static Optional<String> specificErasableFrom(Class<?> a, GenericArrayType b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(Class<?> a, ParameterizedType b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(Class<?> a, TypeVariable<?> b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(Class<?> a, WildcardType b) {
        return failComp("cannot erase %s to a %s", b, a);
    }

    private static Optional<String> specificErasableFrom(Class<?> a, Class<?> b) {
        return onlyIfEqual(a, b);
    }

}
