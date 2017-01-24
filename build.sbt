organization in ThisBuild := "org.thrx"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

updateOptions := updateOptions.value.withCachedResolution(true)

// external services just 4 development
// http://www.lagomframework.com/documentation/1.2.x/java/MultipleBuilds.html
// first use publishLocal on "online-auction-scala" project 
// D:\work-github\lagom-projects\online-auction-scala
lazy val userImpl = lagomExternalProject("user", "com.example" %% "userimpl" % "1.0-SNAPSHOT")
lazy val itemImpl = lagomExternalProject("item", "com.example" %% "itemimpl" % "1.0-SNAPSHOT")
lazy val biddingImpl = lagomExternalProject("bidding", "com.example" %% "biddingimpl" % "1.0-SNAPSHOT")

lazy val `challengerone` = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(`challengerone-api`, `challengerone-impl`, `challengerone-stream-api`, `challengerone-stream-impl`)
  .settings(name := "challengerone")

lazy val `challengerone-api` = (project in file("challengerone-api"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `challengerone-impl` = (project in file("challengerone-impl"))
  .settings(commonSettings: _*)
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
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `challengerone-stream-impl` = (project in file("challengerone-stream-impl"))
  .settings(commonSettings: _*)
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`challengerone-stream-api`, `challengerone-api`)
  
lazy val `challengerone-web` = (project in file("challengerone-web"))
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala && LagomPlay)
  .dependsOn(`challengerone-stream-api`, `challengerone-api`)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslServer,
      macwire,
      scalaTest,
// external start -- TODO remove!
     "com.example" %% "userapi" % "1.0-SNAPSHOT",
     "com.example" %% "itemapi" % "1.0-SNAPSHOT",
     "com.example" %% "biddingapi" % "1.0-SNAPSHOT",

// external end. 
      "org.ocpsoft.prettytime" % "prettytime" % "3.2.7.Final",

      "org.webjars" % "foundation" % "6.2.3",
      "org.webjars" % "foundation-icon-fonts" % "d596a3cfb3"
    )     
  )
  .settings(
     unmanagedSourceDirectories in Compile += baseDirectory.value / "target/scala-2.11/routes/main"
  )
  .settings(
     unmanagedSourceDirectories in Compile += baseDirectory.value / "target/scala-2.11/twirl/main"
  )
  
def commonSettings: Seq[Setting[_]] = Seq(
)

lagomCassandraCleanOnStart in ThisBuild := false

