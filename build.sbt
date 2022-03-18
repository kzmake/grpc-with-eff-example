import Settings._
import scalapb.GeneratorOption.FlatPackage

val baseName = "grpcinscala"

ThisBuild / organization               := "com.github.kzmake"
ThisBuild / scalaVersion               := "2.13.6"
ThisBuild / semanticdbEnabled          := true
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision // only required for Scala 2.x
ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation"
)

lazy val api = (project in file("api"))
  .settings(name := "api")
  .enablePlugins(AkkaGrpcPlugin)
  .settings(protoSettings)
  .settings(Compile / akkaGrpcCodeGeneratorSettings += "server_power_apis")
  .settings(
    Compile / PB.targets += scalapb.validate.gen(
      FlatPackage
    ) -> (Compile / akkaGrpcCodeGeneratorSettings / target).value
  )
  .settings(Compile / akkaGrpcGeneratedSources := Seq(AkkaGrpc.Client, AkkaGrpc.Server))
  .settings(Compile / PB.protoSources += file("api/banking"))

lazy val bankingCtxDomain = (project in file("backend/banking/domain"))
  .settings(name := s"${baseName}-banking-domain")
  .settings(coreSettings)

lazy val bankingCtxUsecase = (project in file("backend/banking/usecase"))
  .settings(name := s"${baseName}-banking-usecase")
  .settings(coreSettings)
  .dependsOn(bankingCtxDomain)

lazy val bankingCtxAdapter = (project in file("backend/banking/adapter"))
  .settings(name := s"${baseName}-banking-adapter")
  .settings(adapterSettings, coreSettings)
  .dependsOn(api, bankingCtxUsecase)

lazy val grpc = (project in file("backend/cmd/grpc"))
  .settings(name := s"${baseName}-grpc")
  .enablePlugins(JavaAgent)
  .settings(
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.10" % "runtime"
  )
  .settings(adapterSettings, coreSettings)
  .dependsOn(bankingCtxAdapter)
