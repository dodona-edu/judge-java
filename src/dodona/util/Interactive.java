package dodona.util;

import dodona.junit.MessageWriter;
import org.junit.Assert;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

/**
 * Allows an interactive application to be tested conveniently.
 */
public class Interactive implements TestRule {
    private final Class<?> cls;
    
    /**
     * TextFromStandardInputStream by default only provides input to the stream
     * once.
     */
    private final Collection<String> inputLines = new ArrayList<>();
    
    private boolean inputStarted = false;
    
    // Rules that are not treated as rules.
    private final MessageWriter feedback = new MessageWriter();
    private final TextFromStandardInputStream stdin = emptyStandardInputStream();
    private final SystemOutRule stdout = new SystemOutRule().enableLog().mute();
    
    /**
     * Interactive constructor.
     *
     * @param cls the class to test
     */
    private Interactive(final Class<?> cls) {
        this.cls = cls;
    }
    
    @Override
    public Statement apply(final Statement base, final Description description) {
        // Ugly hack to use existing Rules as Rules.
        return this.feedback.apply(this.stdin.apply(this.stdout.apply(
            base, description),
            description),
            description);
    }
    
    /**
     * Calls the .main()-method of the class under test.
     *
     * @return fluent
     * @throws Throwable exception thrown by the program
     */
    public Interactive callMain() throws Throwable {
        // Clear anything on stdout before the test starts.
        this.stdout.clearLog();
        
        // Feed the input stream.
        this.stdin.provideLines(this.inputLines.toArray(new String[]{}));
        
        // Execute the main method.
        try {
            final Method main = this.cls.getMethod("main", String[].class);
            final String[] params = new String[0];
            main.invoke(null, (Object) params);
            // Log the output.
            this.logOutput();
        } catch (final NoSuchMethodException e) {
            Assert.fail("Method not found: public static void main(String[])");
        } catch (final IllegalAccessException e) {
            Assert.fail("Method could not be called: public static void main(String[])");
        } catch (final InvocationTargetException e) {
            // Log the output.
            this.logOutput();
            // An exception occurred while running the program.
            throw e.getCause();
        }
        
        // Fluent.
        return this;
    }
    
    /**
     * Feeds double arguments to stdin.
     *
     * @param args the doubles to send to stdin
     * @return fluent
     */
    public Interactive feedLine(final double... args) {
        Arrays.stream(args).mapToObj(Double::toString).forEach(this::feedLine);
        return this;
    }
    
    /**
     * Feeds integer arguments to stdin.
     *
     * @param args the integers to send to stdin
     * @return fluent
     */
    public Interactive feedLine(final int... args) {
        Arrays.stream(args).mapToObj(Integer::toString).forEach(this::feedLine);
        return this;
    }
    
    /**
     * Feeds string arguments to stdin.
     *
     * @param args the text to send to stdin
     * @return fluent
     */
    public Interactive feedLine(final String... args) {
        Arrays.stream(args).forEach(this::feedLine);
        return this;
    }
    
    /**
     * Feeds one input line to stdin and logs the line to the feedback stream.
     *
     * @param line the input line
     */
    private void feedLine(final String line) {
        // Mark the start of input.
        if (!this.inputStarted) {
            this.feedback.println("Input:");
            this.inputStarted = true;
        }
        
        // Append the line to the buffer.
        this.inputLines.add(line);
        
        // Send the line to the feedback stream.
        this.feedback.println(line);
    }
    
    /**
     * Gets an Interactive-instance for the given class.
     *
     * @param cls the class to interact with
     * @return test instance
     */
    public static Interactive forClass(final Class<?> cls) {
        return new Interactive(cls);
    }
    
    private void logOutput() {
        // Write the output to the feedback stream.
        this.feedback.print("\n");
        this.feedback.println("Output:");
        this.feedback.print(this.stdout.getLogWithNormalizedLineSeparator());
    }
    
    /**
     * Gets the contents of stdout as a string.
     *
     * @return the output
     */
    public String output() {
        return this.stdout.getLogWithNormalizedLineSeparator().trim();
    }
    
    /**
     * Gets the contents of stdout as an integer. If the output can not be
     * parsed (or is empty), the test will fail.
     *
     * @return the output parsed as an integer
     */
    public int outputAsInteger() {
        final String output = this.output();
        
        // Attempt to parse the string as an integer.
        try {
            return Integer.parseInt(output);
        } catch (final Exception ex) {
            if (output.isEmpty()) {
                Assert.fail("The application did not produce any output.");
            } else {
                Assert.fail(String.format("The output could not be parsed as an integer: %s", output));
            }
            
            // Unreachable.
            return -1;
        }
    }
    
    /**
     * Gets the contents of stdout as an array of lines.
     *
     * @return the output, split on newlines
     */
    public String[] outputLines() {
        return this.stdout.getLogWithNormalizedLineSeparator().trim().split("\n");
    }
}