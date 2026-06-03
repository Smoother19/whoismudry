ThisBuild / scalaVersion := "2.13.18"
ThisBuild / version := "0.1.0-SNAPSHOT"

val shared = (project in file("shared"))
  .settings(
    name := "whoismudry-shared",
    libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    )
  )

val client = (project in file("client")).dependsOn(shared).settings(
    name := "whoismudry-client",
    libraryDependencies += "ch.hevs.gdx2d" % "gdx2d-desktop" % "1.2.1",
    Compile / unmanagedResourceDirectories += baseDirectory.value / ".." / "assets",
    libraryDependencies += "org.java-websocket" % "Java-WebSocket" % "1.6.0"
  )

val server = (project in file("server"))
  .dependsOn(shared)
  .settings(
    name := "whoismudry-server",
    Compile / unmanagedResourceDirectories += baseDirectory.value / ".." / "assets",
    libraryDependencies += "org.apache.pekko" %% "pekko-actor-typed" % "1.6.0",
    libraryDependencies += "org.java-websocket" % "Java-WebSocket" % "1.6.0"
  )

val root = (project in file("."))
  .aggregate(shared, client, server)
  .settings(
    name := "whoismudry"
  )