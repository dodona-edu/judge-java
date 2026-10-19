package dodona.junit;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExitAgentTest {
    private static final String DIRECTORY = "file:/workdir/";

    // Never run as compiled: each method would end the test JVM.
    public static class Exits {
        public static void systemExit(int status) {
            System.exit(status);
        }

        public static void runtimeExit(int status) {
            Runtime.getRuntime().exit(status);
        }

        public static void runtimeHalt(int status) {
            Runtime.getRuntime().halt(status);
        }

        public static void systemExitReference(int status) {
            IntConsumer exit = System::exit;
            exit.accept(status);
        }

        public static void runtimeExitReference(int status) {
            IntConsumer exit = Runtime.getRuntime()::exit;
            exit.accept(status);
        }
    }

    public static class OwnExit {
        public static void exit(int status) {
        }

        public static void callsOwnExit() {
            exit(1);
        }
    }

    private static ProtectionDomain loadedFrom(String location) throws MalformedURLException {
        return new ProtectionDomain(new CodeSource(URI.create(location).toURL(), (CodeSigner[]) null), null);
    }

    private static byte[] transform(Class<?> fixture, ProtectionDomain domain) throws IOException {
        String internalName = fixture.getName().replace('.', '/');
        try (InputStream in = fixture.getClassLoader().getResourceAsStream(internalName + ".class")) {
            return new ExitAgent().transform(null, internalName, null, domain, in.readAllBytes());
        }
    }

    private static SystemExitError exitFrom(String method, int status) throws Exception {
        byte[] rewritten = transform(Exits.class, loadedFrom(DIRECTORY));
        Class<?> exits = new ClassLoader(ExitAgentTest.class.getClassLoader()) {
            Class<?> define() {
                return defineClass(Exits.class.getName(), rewritten, 0, rewritten.length);
            }
        }.define();
        Method call = exits.getMethod(method, int.class);
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () -> call.invoke(null, status));
        return assertInstanceOf(SystemExitError.class, thrown.getCause());
    }

    static Stream<Named<ProtectionDomain>> notADirectory() throws MalformedURLException {
        return Stream.of(
                Named.of("a jar", loadedFrom("file:/judge/judge.jar")),
                Named.of("a runtime image module", loadedFrom("jrt:/java.base/")),
                Named.of("no code source", new ProtectionDomain(null, null)),
                Named.of("no protection domain", null));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "systemExit, System.exit",
            "runtimeExit, Runtime.exit",
            "runtimeHalt, Runtime.halt",
            "systemExitReference, System.exit",
            "runtimeExitReference, Runtime.exit"})
    void rewritesAnExitCallIntoASystemExitErrorNamingIt(String method, String called) throws Exception {
        assertEquals("Your code called " + called + "(3).", exitFrom(method, 3).getMessage());
    }

    @Test
    void keepsTheExitStatus() throws Exception {
        assertEquals(42, exitFrom("systemExit", 42).getStatus());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("notADirectory")
    void leavesAClassThatIsNotLoadedFromADirectoryAlone(ProtectionDomain domain) throws Exception {
        assertNull(transform(Exits.class, domain));
    }

    @Test
    void leavesAClassThatOnlyCallsItsOwnExitMethodAlone() throws Exception {
        assertNull(transform(OwnExit.class, loadedFrom(DIRECTORY)));
    }
}
