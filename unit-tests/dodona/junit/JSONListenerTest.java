package dodona.junit;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import dodona.KnownBug;
import dodona.feedback.CloseTest;
import dodona.feedback.Message;
import dodona.feedback.StartTest;
import dodona.feedback.Status;
import dodona.feedback.StatusPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.runners.model.TestTimedOutException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JSONListenerTest {

    private static List<JsonObject> written(Consumer<JSONListener> events) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        events.accept(new JSONListener(new PrintStream(out, true, UTF_8)));
        List<JsonObject> commands = new ArrayList<>();
        new JsonStreamParser(out.toString(UTF_8)).forEachRemaining(command -> commands.add(command.getAsJsonObject()));
        return commands;
    }

    private static List<JsonObject> failure(Throwable thrown) {
        return written(listener -> listener.afterTest(TestExecutionResult.failed(thrown)));
    }

    private static JsonObject first(String command, List<JsonObject> commands) {
        return commands.stream()
                .filter(written -> written.get("command").getAsString().equals(command))
                .findFirst()
                .orElseGet(() -> fail("no " + command + " in " + commands));
    }

    private static List<String> names(List<JsonObject> commands) {
        return commands.stream().map(command -> command.get("command").getAsString()).toList();
    }

    private static JsonObject escalation(List<JsonObject> commands) {
        return first("escalate-status", commands).getAsJsonObject("status");
    }

    private static JsonObject testcase(List<JsonObject> commands) {
        return first("start-testcase", commands).getAsJsonObject("description");
    }

    private static JsonObject status(String status, String human) {
        JsonObject pair = new JsonObject();
        pair.addProperty("enum", status);
        pair.addProperty("human", human);
        return pair;
    }

    private static JsonObject message(String format, String description) {
        JsonObject message = new JsonObject();
        message.addProperty("description", description);
        message.addProperty("format", format);
        return message;
    }

    static Stream<Throwable> timeouts() {
        return Stream.of(new TestTimedOutException(1, TimeUnit.SECONDS), new TimeoutException("test timed out"));
    }

    static Stream<Throwable> otherErrors() {
        return Stream.of(new IllegalStateException("broken"), new StackOverflowError(),
                new SystemExitError("System.exit", 1));
    }

    @Test
    void reportsAnAssertionErrorAsWrong() {
        assertEquals(status("wrong", "Wrong"), escalation(failure(new AssertionError("expected 2"))));
    }

    @Test
    void showsTheAssertionMessageAsTheTestcase() {
        assertEquals(message("code", "expected 2"), testcase(failure(new AssertionError("expected 2"))));
    }

    @ParameterizedTest
    @MethodSource("timeouts")
    void reportsATimeoutAsTimeLimitExceeded(Throwable timeout) {
        assertEquals(status("time limit exceeded", "Time limit exceeded"), escalation(failure(timeout)));
    }

    @ParameterizedTest
    @MethodSource("otherErrors")
    void reportsAnyOtherThrowableAsRuntimeError(Throwable error) {
        assertEquals(status("runtime error", "Runtime error"), escalation(failure(error)));
    }

    @Test
    void showsTheExitCallAsTheTestcase() {
        assertEquals(message("plain", "Your code called System.exit(1)."),
                testcase(failure(new SystemExitError("System.exit", 1))));
    }

    @Test
    void showsTheDeepestCauseOfAnExceptionAsTheTestcase() {
        Throwable thrown = new RuntimeException("outer", new IllegalArgumentException("inner"));
        assertEquals(message("code", "java.lang.IllegalArgumentException: inner"), testcase(failure(thrown)));
    }

    @Test
    void reportsATestCarryingThrowableAsItsCarriedTestWithoutEscalating() {
        Throwable thrown = new TestCarryingThrowable(new StartTest("expected"),
                new CloseTest("generated", new StatusPair(Status.WRONG, "Wrong"), false));
        assertEquals(List.of("start-testcase", "start-test", "close-test", "close-testcase", "close-context"),
                names(failure(thrown)));
    }

    @Test
    void reportsAnAnnotatedThrowableAsItsCause() {
        Throwable thrown = new AnnotatedThrowable(new AssertionError("expected 2"), Message.plain("hint"));
        assertEquals(status("wrong", "Wrong"), escalation(failure(thrown)));
    }

    @Test
    void reportsAFailureWithoutThrowableAsUnknownError() {
        assertEquals(status("runtime error", "Unknown Error"), escalation(failure(null)));
    }

    @Test
    void reportsAbortingWithOutOfMemoryAsMemoryLimitExceeded() {
        TestIdentifier test = TestIdentifier.from(new EngineDescriptor(UniqueId.forEngine("unit"), "a test"));
        List<JsonObject> commands = written(listener -> {
            listener.beforeTest(test);
            listener.abortWithOutOfMemory();
        });
        assertEquals(status("memory limit exceeded", "Memory limit exceeded"), escalation(commands));
    }

    @Test
    void reportsAnOutOfMemoryErrorAsMemoryLimitExceeded() {
        KnownBug.expectFailure("a JUnit 4 test that runs out of memory fails with OutOfMemoryError: runtime error",
                () -> assertEquals(status("memory limit exceeded", "Memory limit exceeded"),
                        escalation(failure(new OutOfMemoryError("Java heap space")))));
    }
}
