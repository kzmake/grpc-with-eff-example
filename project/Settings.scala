import sbt._
import sbt.Keys._

object Settings {
  val coreSettings: Def.SettingsDefinition = Seq(
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-Xfatal-warnings",
      "-language:_",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen"
    ),
    libraryDependencies ++= Seq(
      ScalaLogging.core,
      Logback.classic,
      Logstash.logbackEncoder,
      Jackson.scala,
      Eff.core,
      Cats.core,
      ScalaTest.core % Test
    )
  )

  val protoSettings: Def.SettingsDefinition = Seq(
    libraryDependencies ++= Seq(
      GoogleApiGrpc.common % "protobuf-src" intransitive ()
    )
  )

  val infrastructureSettings: Def.SettingsDefinition = Seq(
    libraryDependencies ++= Seq(
      AkkaHttp.http,
      AkkaHttp.http2Support,
      Akka.actorTyped,
      Akka.stream,
      Akka.discovery,
      Akka.pki,
      Akka.actorTestkitTyped % Test,
      Akka.streamTestkit     % Test
    )
  )
}
