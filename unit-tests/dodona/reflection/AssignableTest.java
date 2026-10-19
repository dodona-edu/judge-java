package dodona.reflection;

import dodona.KnownBug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssignableTest {

    // Each method's return type is a type under test, named by the method.
    @SuppressWarnings("unused")
    private interface Types<T, N extends Number> {
        Object object();

        Number number();

        Integer integer();

        String string();

        List<Integer> listOfInteger();

        ArrayList<Integer> arrayListOfInteger();

        List<Number> listOfNumber();

        List<String> listOfString();

        List<?> listOfAny();

        List<? extends Number> listOfSomeNumber();

        List<? super Integer> listOfSuperInteger();

        List<N> listOfN();

        T t();

        N n();

        <U> U u();
    }

    private static Type type(String name) throws NoSuchMethodException {
        return Types.class.getMethod(name).getGenericReturnType();
    }

    private static boolean assignable(String target, String value) throws NoSuchMethodException {
        return Assignable.check(type(target), type(value));
    }

    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource({"object, string", "number, integer", "string, string"})
    void acceptsTheSameClassOrASubclass(String target, String value) throws Exception {
        assertTrue(assignable(target, value));
    }

    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource({"string, object", "integer, string"})
    void rejectsASuperclassOrAnUnrelatedClass(String target, String value) throws Exception {
        assertFalse(assignable(target, value));
    }

    @Test
    void acceptsASubtypeWithTheSameTypeArguments() throws Exception {
        assertTrue(assignable("listOfInteger", "arrayListOfInteger"));
    }

    @Test
    void rejectsATypeArgumentThatIsOnlyASubclass() throws Exception {
        assertFalse(assignable("listOfNumber", "listOfInteger"));
    }

    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource({"listOfSomeNumber, listOfInteger", "listOfSuperInteger, listOfNumber", "listOfAny, listOfString"})
    void acceptsATypeArgumentWithinTheWildcardsBounds(String target, String value) throws Exception {
        assertTrue(assignable(target, value));
    }

    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource({"listOfSomeNumber, listOfString", "listOfSuperInteger, listOfString"})
    void rejectsATypeArgumentOutsideTheWildcardsBounds(String target, String value) throws Exception {
        assertFalse(assignable(target, value));
    }

    @Test
    void acceptsATypeVariableWithTheSameBounds() throws Exception {
        assertTrue(assignable("t", "u"));
    }

    @Test
    void rejectsATypeVariableWithOtherBounds() throws Exception {
        assertFalse(assignable("n", "t"));
    }

    @Test
    void acceptsABoundedTypeVariableAsArgumentOfAnUnboundedWildcard() {
        KnownBug.expectFailure("a wildcard's bounds must equal a type variable's bounds, so List<?> = List<N> fails",
                () -> assertTrue(assignable("listOfAny", "listOfN")));
    }
}
