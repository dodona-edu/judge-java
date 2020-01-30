package dodona.reflection;

import org.junit.Assert;

import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AssertionStubber {

    public <T> T stub(Class<T> expectedInterface, Class<?> solution, Object... constructionParameters) {
        return expectedInterface.cast(Proxy.newProxyInstance(
            expectedInterface.getClassLoader(),
            new Class<?>[] { expectedInterface },
            new AssertingInvocationHandler(solution, constructionParameters)
        ));
    }

    class AssertingInvocationHandler implements InvocationHandler {

        private Class<?> solutionClass;
        private Object solutionInstance;

        public AssertingInvocationHandler(Class<?> solution, Object... constructionParameters) {
            if(Modifier.isAbstract(solution.getModifiers())) testclassIsAbstract();

            Iterator<Constructor<?>> solutionConstructors = Stream
                .of(solution.getDeclaredConstructors()) // returns all constructors (also default)
                .filter(c -> c.getParameterCount() == constructionParameters.length)
                .sorted(Comparator
                    .comparingInt((Constructor c) -> Modifier.isPublic(c.getModifiers()) ? 0 : 1) // prefer public
                    .thenComparing((c1, c2) -> { // prefer specific
                        for(int i = 0; i < constructionParameters.length; i++) {
                            boolean c1c2 = Assignable.check(c1.getGenericParameterTypes()[i], c2.getGenericParameterTypes()[i]);
                            boolean c2c1 = Assignable.check(c2.getGenericParameterTypes()[i], c1.getGenericParameterTypes()[i]);
                            if(c1c2 && !c2c1) return 1; // c2 is more specific
                            if(!c1c2 && c2c1) return -1; // c1 is more specific
                        }
                        return 0; // equal or not comparable
                    }))
                .iterator();

            Constructor<?> constructor = null;
            while(solutionInstance == null && solutionConstructors.hasNext()) {
                constructor = solutionConstructors.next();
                constructor.setAccessible(true); // private should not hinder us (doesn't modify modifiers)
                try {
                    solutionInstance = constructor.newInstance(constructionParameters);
                    solutionClass = solution;
                } catch(IllegalArgumentException e) { // not the right constructor (wrong types)
                } catch(InstantiationException e) { // not the right constructor
                } catch(IllegalAccessException e) { // not the right constructor
                } catch(InvocationTargetException e) { throw new RuntimeException(e.getCause());
                }
            }

            if(solutionInstance == null) missingConstructor(solution, constructionParameters);
            if(!Modifier.isPublic(constructor.getModifiers())) inaccessibleConstructor(constructor);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Optional<Method> solutionMethod = Stream
                .concat(Arrays.stream(solutionClass.getMethods()), // prefer public methods
                        Arrays.stream(solutionClass.getDeclaredMethods())) // but include privates
                .filter(m -> method.getName().equals(m.getName()))
                .filter(m -> method.getParameterCount() == m.getParameterCount())
                .filter(m -> Assignable.check(method.getGenericReturnType(), m.getGenericReturnType()))
                .filter(m -> IntStream
                    .range(0, method.getParameterCount())
                    .allMatch(i -> Assignable.check(
                        method.getGenericParameterTypes()[i],
                        m.getGenericParameterTypes()[i])))
                .findFirst();

            if(!solutionMethod.isPresent()) missingMethod(method);
            if(!Modifier.isPublic(solutionMethod.get().getModifiers())) inaccessibleMethod(method);

            return solutionMethod.get().invoke(solutionInstance, args);
        }

    }

    /* =========================================================================
     * Assertions
     */
    private static void inaccessibleConstructor(final Constructor<?> constructor) {
        Assert.fail("Inaccessible constructor: " + constructor + ". Constructor should have a \"public\" modifier.");
    }
    
    private static void inaccessibleMethod(final Method method) {
        Assert.fail("Inaccessible method: " + describeMethod(method) + ". Method should have a \"public\" modifier.");
    }

    private static void missingConstructor(Class<?> solution, Object... constructionParameters) {
        Assert.fail(String.format("Missing constructor: cannot call %s(%s).",
            solution.getSimpleName(),
            Stream.of(constructionParameters).map(Object::toString).collect(Collectors.joining(", "))
        ));
    }

    private static void missingMethod(Method method) {
        Assert.fail("Missing method: " + describeMethod(method) + ".");
    }

    private static void testclassIsAbstract() {
        Assert.fail("Tested class is abstract");
    }

    private static String describeMethod(final Method method) {
        return String.format("%s %s(%s)",
            method.getReturnType().getSimpleName(),
            method.getName(),
            Stream.of(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(", "))
        );
    }

}
