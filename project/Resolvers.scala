import sbt._

object Resolvers {
  lazy val mavenLocal = Resolver.mavenLocal

  lazy val artifactoryHost = "repo.sfiqautomation.com"
  lazy val artifactoryBaseUrl = s"https://$artifactoryHost/artifactory"

  lazy val releases = "libs-release"             at s"$artifactoryBaseUrl/libs-release/"
  lazy val snapshots = "libs-snapshot"           at s"$artifactoryBaseUrl/libs-snapshot/"
  lazy val pluginsReleases = "plugins-release"   at s"$artifactoryBaseUrl/plugins-release/"
  lazy val pluginsSnapshots = "plugins-snapshot" at s"$artifactoryBaseUrl/plugins-snapshot/"

  lazy val mavenResolvers = Seq(mavenLocal, releases, snapshots, pluginsReleases, pluginsSnapshots)
}
