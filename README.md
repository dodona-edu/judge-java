
# Dodona JUnit Judge

This is a judging system for Dodona, to judge Java exercises using JUnit. Tests are written with JUnit 6 (JUnit Jupiter on the JUnit Platform 6, the launcher in `lib/junit-platform-console-standalone-6.0.2.jar`), on Java 25. [JUnit 4 and JUnit 5 tests](#using-junit-4-or-junit-5) work too.

- [How to create new exercises](#creating-new-exercises)
- [How to run exercises on your computer](#running-exercises)

## Creating new exercises

For exercises descriptions, please check out [this wikipage](https://dodona-edu.github.io/nl/references/exercise-description/).

To write tests for this judge, please read the general instructions on [describing exercises](https://docs.dodona.be/en/guides/exercises/) first. I'll assume you have worked with JUnit before.

### Exercise configuration

Each exercise has a JSON configuration file, for example:

```json
{
  "description": {
    "names": {
      "nl": "Dag Wereld",
      "en": "Hello World"
    }
  },
  "evaluation": {
    "filename": "World.java",
    "handler": "java"
  },
  "visibility": "hidden"
}
```

Please use descriptive names in `description/names/...`, these will be shown to the user, often without context (it's not very informative to have an exercise called "1" in your list of recent exercises if you're following 3 courses at a time).

`evaluation/filename` is the name of the file the students should submit. As this is a Java judge, that's the name of the public class they should submit + `.java`. `evaluation/handler` indicates which judge the exercise should use.

Other configuration values used by the Java judge are:
- `evaluation/allow_compilation_warnings` - A boolean indicating whether the judge should allow compilation warnings, or should fail compilation if warnings were reported. This defaults to `false` if not set, meaning that compilation will fail if warnings occur.
- `evaluation/time_limit` - For how long the tests can continue before a "time limit exceeded" is reported.
- `evaluation/memory_limit` - How much memory the docker is given.
- `evaluation/network_enabled` - Whether the code inside the docker can use the network. Disabled by default and should be enabled sparingly. While we may try to keep everything as secure as possible, a network allows for remote shells. Even if these remote shells have limited permissions, talented students might try to escalate them.

### Where to put which code?

When running the judge, the code is compiled in the order given below. This means each step can use the classes defined in the steps above. The jars in [lib](lib) are available during these steps, except when compiling the code of the student, which only sees the jars in `workdir`.

- The code in `workdir` is compiled. This directory should contain code for everyone, e.g. classes the student can use or interfaces they should implement. It can also contain exercise-specific jar-files.
- The code submitted by the student is compiled.
- The judge's own classes are added to the classpath, from the prebuilt `dist/judge.jar`.
- The code in `evaluation` is compiled. This directory should contain the JUnit tests. It can also contain other classes and jar-files which will be included in the class path.

The test classpath contains the jars in [lib](lib) (gson, junit-platform-console-standalone, random and system-rules) and the jars in `evaluation` and `workdir`. The junit-platform-console-standalone jar bundles the Jupiter, Vintage (JUnit 4) and Suite engines.

After compilation, the judge is executed. It will assume the presence of a `TestSuite` class in the default package, which it runs with the JUnit Platform launcher. While the source file for `TestSuite` can be anywhere, I'd advise `evaluation/TestSuite.java`.

`TestSuite` is a JUnit Platform suite that lists the test classes to run. Each test class in it produces one tab in Dodona:

```java
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    SimpleTest.class,
})
public class TestSuite {}
```

A test class is a regular JUnit Jupiter test class:

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dodona.junit.TabTitle;
import dodona.junit.TestDescription;

@TabTitle("Simple tests")
public class SimpleTest {

    @Test
    @TestDescription("Adding one to 1")
    public void testAddOne() {
        Assertions.assertEquals(2, new Simple().addOne(1));
    }
}
```

### Good practices

While above description leaves a lot of interpretation, here is how a new exercise is written by myself:

- I create a new directory with a `config.json` and the other directories: `description`, `description/media`, `workdir`, `evaluation`, `solution`.
- I write an interface in `workdir`, which the submitted code of the student will have to implement. I might some write other classes in `workdir` to get the student started, or copy some library-jars here (e.g. our graph library for an exercise on graphs).
- I write a solution to the exercise in `solution`. I use the same name as the students should.
- I write a testfile (usually called `SimpleTest.java`) in `evaluation`. This testfile will be shared with the students. This is a pure JUnit test. Usually it contains some small example inputs.
- I write the `TestSuite.java` file in the `evaluation` directory, a testsuite containing only above `SimpleTest.class`.
- (For command line users) I open this repository in a different terminal and go to the `testing` directory. If I pass the exercise directory of this exercise to the `test.sh` script, I can run the judge on my own machine without need for dodona itself. The tested solution is the one in `solution` with the name given in the configuration.
- I create a relative symbolic link in `description/media` pointing to `../../workdir` and another to `../../evaluation/SimpleTest.java`.
- I write the description. In the description I can now link to `media/SimpleTest.java` and `media/workdir/...` to give the `SimpleTest` and other files in the `workdir` to the student.
- I write some more complex tests (usually with Jupiter's `@ParameterizedTest` or `@ParameterizedClass`), testing each with method above. These tests I put in `evaluation`, because they often contain a solution to the exercise.

### Test files

As mentioned, test files can be pure JUnit Jupiter test files. Working examples are in the [examples](examples) directory, and the [integration tests](integration-tests) cover most of the features below, for JUnit 4 (`junit4`), for Jupiter (`junit6`) or both (`combined`). Some extras are available though:

- By importing `dodona.junit.TabTitle` in your exercise, you can set the title of the tab in dodona for this test (each test class in TestSuite will produce one tab). Just add the `@TabTitle("Some Title")` annotation to your test class. Without it, the tab is named after the test class (or its `@DisplayName`).
- `dodona.junit.TestDescription` sets the title of a single test. Add `@TestDescription("Some description")` to the test method. Without it, the title is the name of the test method (`testAddOne()`).
- `dodona.junit.TabPermission` sets who can see the tab. Add `@TabPermission(Permission.EDUCATOR)` to your test class (`Permission` is `dodona.feedback.Permission`) to show the tab to educators only. Tabs are visible to students by default. `Permission.STAFF` is deprecated, use `Permission.EDUCATOR`.
- `dodona.junit.MessageWriter` lets you write to `out` as if it were `System.out` (it's actually a `PrintWriter`, not a `PrintStream`, but they share most methods). Whatever you write to it in a test will be visible to the students if that test fails. Useful for e.g. printing the input matrices to a matrix multiplication exercise test, along with the expected and generated outputs. Declare it as a Jupiter extension with `@RegisterExtension public MessageWriter out = new MessageWriter();` (`org.junit.jupiter.api.extension.RegisterExtension`).
- `dodona.junit.ExpectedSystemExit` checks that the code under test calls `System.exit`. Declare it as a Jupiter extension with `@RegisterExtension public ExpectedSystemExit exit = ExpectedSystemExit.none();` and call `exit.expectSystemExit()` or `exit.expectSystemExitWithStatus(3)` in a test. It replaces the `ExpectedSystemExit` of system-rules, which doesn't work on current Java, and has the same methods.
- `dodona.junit.TestWriter` compares an expected and a received value, with `compare(expected, received)` (optionally with a description first, or with a delta for doubles). When they differ, the test fails and Dodona shows both values. Create it as a plain object, for example `private static dodona.junit.TestWriter testWriter = new dodona.junit.TestWriter();`. No extension is needed.
- `dodona.reflection.AssertionStubber` lets your tests call the submitted class through an interface: `new AssertionStubber().stub(SomeInterface.class, Submitted.class, constructorArguments...)`. If the class, a constructor or a method is missing, not public or has the wrong types, the test fails with a message saying so.

### Using JUnit 4 or JUnit 5

The judge also runs tests written for JUnit 4 or JUnit 5.

**JUnit 5** (Jupiter 5.x) test classes run unchanged on JUnit 6, and so does a `@Suite` + `@SelectClasses` `TestSuite`. Only the APIs removed in [JUnit 6](https://docs.junit.org/6.0.0/release-notes/) need changes, such as `MethodOrderer.Alphanumeric` and the `lineSeparator` attribute of `@CsvFileSource`. A `@RunWith(JUnitPlatform.class)` suite has to become a `@Suite` (the `junit-platform-runner` module is gone), and `junit-jupiter-migrationsupport` is not on the classpath. Parameterized tests also get new display names in JUnit 6 (text arguments are quoted), which Dodona shows as the test title.

**JUnit 4** tests use the JUnit 4 forms of the same things:

- `TestSuite` is a JUnit 4 suite:

  ```java
  import org.junit.runner.RunWith;
  import org.junit.runners.Suite;

  @RunWith(Suite.class)
  @Suite.SuiteClasses({
      SimpleTest.class,
  })
  public class TestSuite {}
  ```

- Test classes use `org.junit.Test` and `org.junit.Assert`. Parameterized tests use the `Parameterized` runner (`@RunWith(Parameterized.class)`).
- `MessageWriter` is a rule: `@Rule public MessageWriter out = new MessageWriter();`.
- `ExpectedSystemExit` is a rule: `@Rule public ExpectedSystemExit exit = ExpectedSystemExit.none();`. Import `dodona.junit.ExpectedSystemExit` instead of `org.junit.contrib.java.lang.system.ExpectedSystemExit`.
- `dodona.junit.MultiMessageWriter` is a JUnit 4 rule that collects several messages: `append(...)` adds one, `remove(...)` and `removeIf(...)` take them back. It is not a Jupiter extension.
- Without `@TestDescription`, the title of a test is the name of the method (`testAddOne`, without the parentheses).

JUnit 4 and Jupiter test classes can be mixed in one suite if `TestSuite` is a JUnit Platform suite (see `integration-tests/combined/simple`).

## Running Exercises

Creating/modifying an exercise, committing your changes, pushing them to your remote, and submitting the solution on Dodona makes for quite a slow development cycle. To run your tests locally, various methods are possible.

### Using shellscripts

If you're familiar with the command line, this repository provides some shell scripts to test your exercises on your POSIX-compatible machine, with a JDK installed. With `testing` as working directory, run `./test.sh path/to/the/exercise/`. This path points to a directory with a `config.json` exercise file in it. It will "submit" the solution in the `solution` subdirectory. Reading the JSON output might take some getting used to.

To handle JSON in the shell, this script (and the judge) require [jq](https://jqlang.org/) to be installed.

### Using IntelliJ (or other IDE's)

Since the tests are mostly just jUnit tests, IntelliJ and other IDE's provide support for running them. The directory structure makes things slightly more complicated, though. This describes one method to get things running, people more familiar with IntelliJ might know a better way.

Per exercise, create a new project. The `config.json` file should be in the project root. Mark (or create) the `workdir`, `evaluation` and `solution` directories as "sources root". Add JUnit Jupiter (version 6) as a dependency of the project, or JUnit 4 if you write JUnit 4 tests.

If your exercises use some Dodona-specific features, such as the `TabTitle` or the `AssertionStubber`, add the judge as a dependency: `dist/judge.jar` in this repository.

## Developing the judge

While developing, you can use `./test.sh` in the root of this repository to validate whether the judge is working properly. It runs `./integration-tests/run` in the `ghcr.io/dodona-edu/dodona-java` judge image, like CI does, so you need Docker. Set `IMAGE` to run in another image. Extra arguments are passed on to `integration-tests/run`, for example `./test.sh -v`. On an arm64 Mac, set `DOCKER_DEFAULT_PLATFORM=linux/amd64`.

`integration-tests/run` checks the judge's output JSON against previous results (stored as `result.json`) and monitors whether something has changed. The cases are in `integration-tests/junit4` (JUnit 4), `integration-tests/junit6` (JUnit Platform and Jupiter) and `integration-tests/combined` (both in one suite).

You can also use `./integration-tests/run <path-to-repo>` to run the same checks on an exercise repository, which is useful to validate a larger set of exercises. Give the path relative to the root of this repository. This runs the judge directly, so it needs the judge's environment (a Linux machine with a JDK and jq); `./test.sh` only mounts this repository.

With `--overwrite`, you can overwrite previous `result.json` files with the current output, and with `-v` you can check for judge error output and view the exact changes when output differs. `./test.sh` mounts the repository read-only, so use `./integration-tests/run --overwrite` directly for that.

The judge runs from the prebuilt `dist/judge.jar`, not from `src/`: Dodona has no build step. After changing anything in `src/`, run `./devel/build-jar.sh` (Docker; it builds the jar reproducibly in the judge image) and commit `dist/judge.jar` along with your change. CI fails when the committed jar doesn't match a fresh build of `src/`; `./devel/build-jar.sh --check` runs the same check locally.
