// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.3.0-M1")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

// TODO: THRXDE: not ready jet
// // packager plugin
// lazy val root = Project("plugins", file(".")).dependsOn(plugin)

// lazy val plugin = file("../").getCanonicalFile.toURI

// // needs to be added for the docker spotify client
// libraryDependencies += "com.spotify" % "docker-client" % "3.5.13"

