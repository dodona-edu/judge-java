
# Dodona JUnit Judge

This is a judging system for Dodona, to judge Java exercises using the JUnit 4 testing framework.

- [How to create new exercises](#creating-new-exercises)
- [How to run exercises on your computer](#running-exercises)

## Creating new exercises

For exercises descriptions, please check out [this wikipage](https://dodona-edu.github.io/nl/references/exercise-description/).

To write tests for this judge, please read the general instructions on [describing exercises](https://dodona-edu.github.io/en/guides/new-exercise-repo/) first. I'll assume you have worked with JUnit before.

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

When running the judge, the code is compiled in the order given below. This means each step can use the classes defined in the steps above. During the compilation of these files, the jars in [lib](lib) are available.

- The code in `workdir` is compiled. This directory should contain code for everyone, e.g. classes the student can use or interfaces they should implement. It can also contain exercise-specific jar-files.
- The code submitted by the student is compiled.
- The judge is compiled.
- The code in `evaluation` is compiled. This directory should contain the JUnit tests. It can also contain other classes and jar-files which will be included in the class path.

After compilation, the judge is executed. It will assume the presence of a `TestSuite` class, containing a JUnit 4 testsuite, which it will run. While the source file for `TestSuite` can be anywhere, I'd advise `evaluation/TestSuite.java`.

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
- I write some more complex tests (usually with JUnit's `Parameterized` runner), testing each with method above. These tests I put in `evaluation`, because they often contain a solution to the exercise.

### Test files

As mentioned, test files can be pure JUnit test files, including all features in JUnit 4 (I especially recommend Parameterized). Some extras are available though:

- By importing `dodona.junit.TabTitle` in your exercise, you can set the title of the tab in dodona for this test (each test in TestSuite will produce one tab). Just add the `@TabTitle("Some Title")` annotation to your test class.
- `dodona.junit.MessageWriter` is a JUnit test rule. By using `@Rule public MessageWriter out = new MessageWriter();` in your test class, you can write to `out` as if it were `System.out` (it's actually a `PrintWriter`, not a `PrintStream`, but they share most methods). Whatever you write to it in a test will be visible to the students if that test fails. Useful for e.g. printing the input matrices to a matrix multiplication exercise test, along with the expected and generated outputs.

## Running Exercises

Creating/modifying and exercise, commiting your changes, pushing them to your remote, and submitting the solution on Dodona makes for quite a slow development cycle. To run you tests locally, various methods are possible.

### Using shellscripts

If you're familiar with the command line, this repository provides some shell scripts to test your exercises on your POSIX-compatible machine. With `testing` as working directory, run `./test.sh path/to/the/exercise/`. This path points to a directory with a `config.json` exercise file in it. It will "submit" the solution in the `solution` subdirectory. Reading the JSON output might take some getting used to.

To handle JSON in the shell, this script (and the judge) require [jq](https://stedolan.github.io/jq/) to be installed.

### Using IntelliJ (or other IDE's)

Since the tests are mostly just jUnit tests, IntelliJ and other IDE's provide support for running them. The directory structure makes things slightly more complicated, though. This describes one method to get things running, people more familiar with IntelliJ might know a better way.

Per exercise, create a new project. The `config.json` file should be in the project root. Mark (or create) the `workdir`, `evaluation` and `solution` directories as "sources root". Add jUnit 4 as a dependency of the project.

If your exercises use some Dodona-specific features, such as the `TabTitle` or the `AssertionStubber`, add the Judge as a dependency. Opening this repository as an IntelliJ project should allow you to create a JAR.
