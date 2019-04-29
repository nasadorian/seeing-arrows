# seeing-arrows

Seeing Arrows Below the Code [2019 Conference Talk]

## Development

To start an interactive SBT session, type `sbt` at the prompt:

```
» sbt
```

From inside this session, you can execute any of the standard SBT tasks below:

```
clean            Deletes files produced by the build, such as generated sources, compiled classes, and task caches.
compile          Compiles sources.
console          Starts the Scala interpreter with the project classes on the classpath.
consoleProject   Starts the Scala interpreter with the sbt and the build definition on the classpath and useful imports.
consoleQuick     Starts the Scala interpreter with the project dependencies on the classpath.
copyResources    Copies resources to the output directory.
doc              Generates API documentation.
package          Produces the main artifact, such as a binary jar.  This is typically an alias for the task that actually does the packaging.
packageBin       Produces a main artifact, such as a binary jar.
packageDoc       Produces a documentation artifact, such as a jar containing API documentation.
packageSrc       Produces a source artifact, such as a jar containing sources and resources.
publish          Publishes artifacts to a repository.
publishLocal     Publishes artifacts to the local Ivy repository.
publishM2        Publishes artifacts to the local Maven repository.
run              Runs a main class, passing along arguments provided on the command line.
runMain          Runs the main class selected by the first argument, passing the remaining arguments to the main method.
scalastyle       Run scalastyle on your code
test             Executes all tests.
testOnly         Executes the tests provided as arguments or all tests if no arguments are provided.
testQuick        Executes the tests that either failed before, were not run or whose transitive dependencies changed, among those provided as arguments.
update           Resolves and optionally retrieves dependencies, producing a report.
```

Start a Scala console (REPL) session in the usual way, by running the `console` task, as shown below:

```
> console
[info] Compiling 1 Scala source to /Users/spoderman/projects/herp/target/scala-2.11/classes...
[info] Starting scala interpreter...
[info]
Welcome to Scala 2.11.8 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_25).
Type in expressions for evaluation. Or try :help.

scala>
```

This starts an interactive Scala session, with the classpath of your project sources.

## Contributing

Contributions are reviewed and accepted via pull request. To create one, perform the following steps:

1. Create a feature branch
2. Implement your contribution
3. Write tests
4. Write documentation
5. Validate changes
6. Submit pull request

Before submitting a pull request, ensure that your feature branch builds cleanly with the `validate` command alias, which will run additional static code analysis with [scalastyle][scalastyle] and [WartRemover][wartremover] before running the unit tests, as follows:

```
> validate
[info] scalastyle using config /Users/spoderman/projects/herp/scalastyle-config.xml
[info] Processed 1 file(s)
[info] Found 0 errors
[info] Found 0 warnings
[info] Found 0 infos
[info] Finished in 1 ms
[success] created output: /Users/spoderman/projects/herp/target
[success] Total time: 0 s, completed Jan 31, 2017 2:08:17 PM
[info] Compiling 1 Scala source to /Users/spoderman/projects/herp/target/scala-2.11/classes...
[info] HerpSpec:
[info] ScalaTest
[info] Run completed in 108 milliseconds.
[info] Total number of tests run: 0
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 0, failed 0, canceled 0, ignored 0, pending 0
[info] No tests were executed.
[info] Passed: Total 0, Failed 0, Errors 0, Passed 0
[success] Total time: 1 s, completed Jan 31, 2017 2:08:17 PM
```

## Building

The release process for seeing-arrows makes use of the [sbt-release][sbt-release] plugin. The release steps are as follows:

1. Check for `SNAPSHOT` dependencies
2. Clean generated files
3. Run the `validate` command to compile and test sources
4. Set the release version of the JAR
5. Commit the release version
6. Tag the release in Github
7. Publish project artifacts
8. Set the next development version of the project
9. Commit the next version
9. Push changes to Github

This can be done interactively by running the `release` task, or non-interactively by running the `release with-defaults` task, to accept all defaults. The defaults are almost always sensible.

[scalastyle]: http://www.scalastyle.org/sbt.html
[wartremover]: https://github.com/wartremover/wartremover
[sbt-release]: https://github.com/sbt/sbt-release
