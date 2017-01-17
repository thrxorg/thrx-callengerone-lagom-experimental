organization in ThisBuild := "org.thrx"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `challengerone` = (project in file("."))
  .aggregate(`challengerone-api`, `challengerone-impl`, `challengerone-stream-api`, `challengerone-stream-impl`)

lazy val `challengerone-api` = (project in file("challengerone-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `challengerone-impl` = (project in file("challengerone-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`challengerone-api`)

lazy val `challengerone-stream-api` = (project in file("challengerone-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `challengerone-stream-impl` = (project in file("challengerone-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`challengerone-stream-api`, `challengerone-api`)

