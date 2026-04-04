ThisBuild / organization := "com.alpha"
ThisBuild / version := "1.0.0"
ThisBuild / libraryDependencySchemes ++= Seq(
  "dev.zio" %% "zio-json" % "early-semver"
)

scalaVersion := "3.4.0"

val zioVersion = "2.1.0"
val zioHttpVersion = "3.8.1"
val zioPreludeVersion = "1.0.0-RC47"
val zioJsonVersion = "0.7.42"
val quillVersion = "4.8.6"
val testContainersVersion = "0.8.0"
val jwtScalaVersion = "10.0.4"
val tapirVersion = "1.13.15"

val zioConfigVersion = "4.0.4"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
)

lazy val root = (project in file("."))
  .settings(
    name := "alpha-backend",
    Compile / run / mainClass := Some("com.alpha.Main"),
    test / parallelExecution := false
  )
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-streams" % zioVersion,
      "dev.zio" %% "zio-prelude" % zioPreludeVersion,
      "dev.zio" %% "zio-macros" % zioVersion,
       "dev.zio" %% "zio-http" % zioHttpVersion,
       "dev.zio" %% "zio-json" % zioJsonVersion,
       "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
       "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapirVersion,
       "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
       "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
       "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
       "dev.zio" %% "zio-config" % zioConfigVersion,
       "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
       "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
       "io.getquill" %% "quill-jdbc" % quillVersion,
       "org.postgresql" % "postgresql" % "42.7.0",
       "com.zaxxer" % "HikariCP" % "5.1.0",
       "com.github.jwt-scala" %% "jwt-core" % jwtScalaVersion,
       "com.github.jwt-scala" %% "jwt-zio-json" % jwtScalaVersion,
       "org.mindrot" % "jbcrypt" % "0.4",
      "org.flywaydb" % "flyway-core" % "10.17.0",
      "org.flywaydb" % "flyway-database-postgresql" % "10.17.0",
      "org.typelevel" %% "case-insensitive" % "1.4.0",
      "org.yaml" % "snakeyaml" % "2.2",
      "org.slf4j" % "slf4j-api" % "2.0.14",
      "ch.qos.logback" % "logback-classic" % "1.4.14",
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-junit" % zioVersion % Test,
      "com.github.sideeffffect" %% "zio-testcontainers" % testContainersVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalacheck" %% "scalacheck" % "1.17.0" % Test,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.41.0" % Test
    )
  )

addCommandAlias("run", "run")
addCommandAlias("test", "test")
addCommandAlias("console", "console")
