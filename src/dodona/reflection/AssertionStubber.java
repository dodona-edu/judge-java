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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AssertionStubber {


    /* =========================================================================
     * Civilisation
     */
    private static final Map<Class<?>, Class<?>> primitives = new HashMap<>();
    static {
        primitives.put(Boolean.class, boolean.class);
        primitives.put(Character.class, char.class);
        primitives.put(Byte.class, byte.class);
        primitives.put(Short.class, short.class);
        primitives.put(Integer.class, int.class);
        primitives.put(Long.class, long.class);
        primitives.put(Float.class, float.class);
        primitives.put(Double.class, double.class);
        primitives.put(Void.class, void.class);
    }

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
            // Getting the relevant constructor.
            Constructor<?> constructor = null;
            Class<?>[] constructionParameterTypes = new Class<?>[constructionParameters.length];
            for(int i = 0; i < constructionParameters.length; i++) {
                constructionParameterTypes[i] = primitives.getOrDefault(
                    constructionParameters[i].getClass(),
                    constructionParameters[i].getClass());
            }
            try {
                constructor = solution.getConstructor(constructionParameterTypes);
            } catch(NoSuchMethodException e) {
                // Constructor might have a non-public visibility, attempt to
                // find it.
                try {
                    solution.getDeclaredConstructor(constructionParameterTypes);
                    // This point can only be reached if the above call did not
                    // throw an exception -> constructor exists but is not
                    // public.
                    inaccessibleConstructor(solution, constructionParameterTypes);
                } catch (final NoSuchMethodException ex) {
                    // Constructor not found -> really doesn't exist.
                    missingConstructor(solution, constructionParameterTypes);
                }
            }

            solutionClass = solution;
            try {
                solutionInstance = constructor.newInstance(constructionParameters);
            } catch(InstantiationException e) { testclassIsAbstract();
            } catch(IllegalAccessException e) { illegalConstructorAccess();
            } catch(InvocationTargetException e) { throw new RuntimeException(e.getCause());
            }
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
    private static void inaccessibleConstructor(final Class<?> solution, final Class<?>... constructionParameterTypes) {
        Assert.fail("Inaccessible constructor: " + missingMethod(solution.getSimpleName(), constructionParameterTypes) + ". Constructor should have a \"public\" modifier.");
    }
    
    private static void inaccessibleMethod(final Method method) {
        Assert.fail("Inaccessible method: " + missingMethod(method.getReturnType().getSimpleName() + " " + method.getName(), method.getParameterTypes()) + ". Method should have a \"public\" modifier.");
    }
    
    private static String missingMethod(String returnName, Class<?>... parameterTypes) {
        return returnName + "(" + Stream.of(parameterTypes).map(Class::getSimpleName).collect(Collectors.joining(",")) + ")";
    }

    private static void missingConstructor(Class<?> solution, Class<?>... constructionParameterTypes) {
        Assert.fail("Missing constructor: " + missingMethod(solution.getSimpleName(), constructionParameterTypes));
    }

    private static void missingMethod(Method method) {
        Assert.fail("Missing method: " + missingMethod(method.getReturnType().getSimpleName() + " " + method.getName(), method.getParameterTypes()));
    }

    private static void testclassIsAbstract() {
        Assert.fail("Tested class is abstract");
    }

    private static void illegalConstructorAccess() {
        Assert.fail("Illegal Constructor access");
    }

}
