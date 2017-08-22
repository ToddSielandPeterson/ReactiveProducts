organization in ThisBuild := "com.cognitivecreations"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val jodaMoney = "org.joda" % "joda-money" % "0.12"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `hello-lagom` = (project in file("."))
  .aggregate(`reactive-products-api`, `reactive-products-impl`, `reactive-products-stream-api`, `reactive-products-stream-impl`)

lazy val `reactive-products-api` = (project in file("reactive-products-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      jodaMoney
    )
  )

lazy val `reactive-products-impl` = (project in file("reactive-products-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`reactive-products-api`)

lazy val `reactive-products-stream-api` = (project in file("reactive-products-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      jodaMoney
    )
  )

lazy val `reactive-products-stream-impl` = (project in file("reactive-products-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`reactive-products-stream-api`, `reactive-products-api`)
