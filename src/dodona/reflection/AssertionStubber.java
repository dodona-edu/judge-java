package dodona.reflection;

import org.junit.Assert;

import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            } catch(NoSuchMethodException e) { missingConstructor(solution, constructionParameterTypes); }

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
            Optional<Method> solutionMethod = Optional.empty();
            try {
                solutionMethod = Optional.of(solutionClass.getMethod(method.getName(), method.getParameterTypes()));
            } catch(NoSuchMethodException e) {
                solutionMethod = Arrays.stream(solutionClass.getMethods())
                    .filter(c -> method.getName().equals(c.getName()))
                    .filter(c -> method.getParameterCount() == c.getParameterCount())
                    .filter(c -> method.getReturnType().isAssignableFrom(c.getReturnType()))
                    .filter(c -> IntStream
                        .range(0, method.getParameterCount())
                        .allMatch(i -> c.getParameterTypes()[i].isInstance(args[i])))
                    .findFirst();
            }

            if(solutionMethod.isPresent()) {
                return solutionMethod.get().invoke(solutionInstance, args);
            } else {
                missingMethod(method);
                return null;
            }
        }

    }

    /* =========================================================================
     * Assertions
     */
    private String missingMethod(String returnName, Class<?>... parameterTypes) {
        return returnName + "(" + Stream.of(parameterTypes).map(Class::getSimpleName).collect(Collectors.joining(",")) + ")";
    }

    private void missingConstructor(Class<?> solution, Class<?>... constructionParameterTypes) {
        Assert.fail("Missing constructor: " + missingMethod(solution.getSimpleName(), constructionParameterTypes));
    }

    private void missingMethod(Method method) {
        Assert.fail("Missing method: " + missingMethod(method.getReturnType().getSimpleName() + " " + method.getName(), method.getParameterTypes()));
    }

    private void testclassIsAbstract() {
        Assert.fail("Tested class is abstract");
    }

    private void illegalConstructorAccess() {
        Assert.fail("Illegal Constructor access");
    }

}
