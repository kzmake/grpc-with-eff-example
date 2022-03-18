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
      Atnos.eff,
      Scalatest.scalatest % Test
    )
  )

  val protoSettings: Def.SettingsDefinition = Seq(
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "scalapb-validate-core" % scalapb.validate.compiler.BuildInfo.version % "protobuf",
      GoogleApiGrpc.protoGoogleCommonProtos % "protobuf-src" intransitive ()
    )
  )

  val adapterSettings: Def.SettingsDefinition = Seq(
    libraryDependencies ++= Seq(
      TypesafeAkka.akkaHttp,
      TypesafeAkka.akkaHttpCore,
      TypesafeAkka.akkaHttp2Support,
      TypesafeAkka.akkaActorTyped,
      TypesafeAkka.akkaStream,
      TypesafeAkka.akkaDiscovery,
      TypesafeAkka.akkaPki,
      TypesafeAkka.akkaActorTestkitTyped % Test,
      TypesafeAkka.akkaStreamTestkit     % Test
    )
  )
}
