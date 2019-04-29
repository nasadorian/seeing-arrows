import sbt._

object Dependencies {
  lazy val scalactic  = "org.scalactic"  %% "scalactic"  % "3.0.1"
  lazy val scalaTest  = "org.scalatest"  %% "scalatest"  % "3.0.1"  % "test"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

  // Grouped dependencies
  lazy val testing = Seq(scalactic, scalaTest, scalaCheck)

  lazy val cats = "org.typelevel" %% "cats-core" % "1.6.0"

}
