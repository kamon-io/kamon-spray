lazy val root: Project = project.in(file(".")).dependsOn(latestSbtUmbrella)
lazy val latestSbtUmbrella = ProjectRef(uri("git://github.com/kamon-io/kamon-sbt-umbrella.git"), "kamon-sbt-umbrella")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.7")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")