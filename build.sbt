organization in ThisBuild := "org.thrx"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "3.3"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"

updateOptions := updateOptions.value.withCachedResolution(true)

lazy val `challengerone` = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(cOneBiddingApi,
        cOneBiddingImpl, 
  		cOneItemApi, 
  		cOneItemImpl, 
//  		cOneSearchApi, 
//  		cOneSearchImpl, 
//  		cOneSecurity, 
//  		cOneTransactionApi,
//  		cOneTransactionImpl,
  		cOneUserApi, 
  		cOneUserImpl
  		)
  .settings(name := "challengerone")

lazy val cOneSecurity = (project in file("c-one-security"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      playJsonDerivedCodecs,
      scalaTest
    )
  )

lazy val cOneItemApi = (project in file("c-one-item-api"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )
  .dependsOn(cOneSecurity)

lazy val cOneItemImpl = (project in file("c-one-item-impl"))
  .settings(commonSettings: _*)
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      lagomScaladslKafkaBroker,
      "com.datastax.cassandra" % "cassandra-driver-extras" % "3.0.0",
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(cOneItemApi, cOneBiddingApi)

lazy val cOneBiddingApi = (project in file("c-one-bidding-api"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )
  .dependsOn(cOneSecurity)

lazy val cOneBiddingImpl = (project in file("c-one-bidding-impl"))
  .settings(commonSettings: _*)
  .enablePlugins(LagomScala)
  .dependsOn(cOneBiddingApi, cOneItemApi)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      lagomScaladslKafkaBroker,
      macwire,
      scalaTest
    ),
    maxErrors := 10000

  )

lazy val cOneSearchApi = (project in file("c-one-search-api"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )
  .dependsOn(cOneSecurity)

lazy val cOneSearchImpl = (project in file("c-one-search-impl"))
  .settings(commonSettings: _*)
  // .enablePlugins(LagomScala)
  .dependsOn(cOneSearchApi, cOneItemApi, cOneBiddingApi)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )

lazy val cOneTransactionApi = (project in file("c-one-transaction-api"))
  .settings(commonSettings: _*)
  .dependsOn(cOneItemApi)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )
  .dependsOn(cOneSecurity)

lazy val cOneTransactionImpl = (project in file("c-one-transaction-impl"))
  .settings(commonSettings: _*)
  // .enablePlugins(LagomScala)
  .dependsOn(cOneTransactionApi, cOneBiddingApi)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )

lazy val cOneUserApi = (project in file("c-one-user-api"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )
  .dependsOn(cOneSecurity)

lazy val cOneUserImpl = (project in file("c-one-user-impl"))
  .settings(commonSettings: _*)
  .enablePlugins(LagomScala)
  .dependsOn(cOneUserApi)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      macwire,
      scalaTest
    )
  )


  
lazy val cOneWeb = (project in file("c-one-web"))
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala && LagomPlay)
  .dependsOn(cOneBiddingApi, cOneItemApi, cOneUserApi)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslServer,
      macwire,
      scalaTest,
      
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

