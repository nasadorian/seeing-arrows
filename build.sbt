import ReleaseTransformations._
import wartremover.Wart


///////////////////////////////////////////////////////////
// Basic build settings

lazy val buildSettings = Seq(
  name         := "seeing-arrows",
  organization := "portal",
  scalaVersion := "2.12.8"
)

///////////////////////////////////////////////////////////
// Maven credentials settings

def getMavenCredentials(file: File): Credentials = {
  val realm = "Artifactory Realm"
  val host  = Resolvers.artifactoryHost
  val creds = (xml.XML.loadFile(file) \ "servers" \ "server").head
  Credentials(realm, host, (creds \ "username").text, (creds \ "password").text)
}

///////////////////////////////////////////////////////////
// Common build settings

lazy val commonSettings = Seq(
  credentials                     += getMavenCredentials(Path.userHome / ".m2" / "settings.xml"),
  resolvers                      ++= Resolvers.mavenResolvers,
  parallelExecution in Test       := false,
  connectInput                    := true,
  updateOptions                   := updateOptions.value.withCachedResolution(true),
  scalastyleFailOnError           := true,
  scalacOptions                   := commonScalacOptions,
  scalacOptions in (Compile, doc) := (scalacOptions in (Compile, doc)).value filterNot (_ == "-Xfatal-warnings"),

  libraryDependencies ++= Seq(Dependencies.cats),
  // Wart Remover settings
  wartremoverErrors in (Compile, compile) ++= Seq(
    Wart.EitherProjectionPartial, // bans calling `get` on an `Either.LeftProjection` or `Either.RightProjection`
    Wart.JavaConversions,         // bans use of the wrong Java conversions (use `scala.collection.JavaConverters` instead)
    Wart.OptionPartial,           // bans calling `get` on an `Option` (use `fold` instead)
    Wart.Product,                 // bans inference of `Product` as a generic type
    Wart.Return,                  // bans use of `return`
    Wart.Serializable,            // bans inference of `Serializable` as a generic type
    Wart.StringPlusAny,           // bans implicit conversion to `String`
    Wart.TraversableOps,          // bans unsafe operations on `Traversable` (e.g., `head`)
    Wart.TryPartial               // bans calling `get` on a `Try` (use `Try#map` and `Try#getOrElse` instead)
  )
) ++ warnUnusedImport ++ inlineWarnings

///////////////////////////////////////////////////////////
// Publish settings

publishTo := {
	if (isSnapshot.value) Some(Resolvers.snapshots) else Some(Resolvers.releases)
}

///////////////////////////////////////////////////////////
// Release settings

lazy val releaseSettings = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    releaseStepCommand("scalastyle"),
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

///////////////////////////////////////////////////////////
// Project definitions

lazy val root = Project("seeing-arrows", file("." + "seeing-arrows")).in(file("."))
  .settings(buildSettings: _*)
  .settings(commonSettings: _*)
  .settings(releaseSettings: _*)
  .settings(libraryDependencies ++= Dependencies.testing)

///////////////////////////////////////////////////////////
// Scala compiler settings

lazy val commonScalacOptions = Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.0")

lazy val warnUnusedImport = Seq(
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 11 => Seq("-Ywarn-unused-import")
      case _                       => Seq()
    }
  },
  scalacOptions in (Compile, console) := (scalacOptions in (Compile, console)).value filterNot Set(
    "-Ywarn-unused-import",
    "-Xfatal-warnings"
  ),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
)

lazy val inlineWarnings = Seq(
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      // scalastyle:off magic.number
      case Some((2, 12)) => Seq()
      case _             => Seq("-Yinline-warnings")
      // scalastyle:on
    }
  }
)

///////////////////////////////////////////////////////////
// Command aliases

addCommandAlias("validate", ";scalastyle;test")
