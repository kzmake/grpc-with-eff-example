import akka.grpc.sbt.AkkaGrpcPlugin.autoImport.akkaGrpcCodeGeneratorSettings
import sbt._
import sbt.Keys._
import sbtprotoc.ProtocPlugin.autoImport.PB
import scalapb.GeneratorOption._

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
      ScalaTest.core % Test
    )
  )

  val protoSettings: Def.SettingsDefinition = Seq(
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "scalapb-validate-core" % scalapb.validate.compiler.BuildInfo.version % "protobuf",
      GoogleApiGrpc.common    % "protobuf-src" intransitive ()
    )
  )

  val adapterSettings: Def.SettingsDefinition = Seq(
    libraryDependencies ++= Seq(
      AkkaHttp.http,
      AkkaHttp.core,
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
