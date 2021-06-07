import Dependencies._

ThisBuild / scalaVersion     := "2.13.6"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.marimon"
ThisBuild / organizationName := "marimon"

lazy val root = (project in file("."))
  .settings(
    name := "consums",
    libraryDependencies += scalaTest % Test
  )

