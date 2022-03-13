import Settings._

val baseName = "grpcinscala"

ThisBuild / organization               := "com.github.kzmake"
ThisBuild / scalaVersion               := "2.13.6"
ThisBuild / semanticdbEnabled          := true
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision // only required for Scala 2.x
ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Wunused:imports" // Scala 2.x only, required by `RemoveUnused`
)

lazy val api = (project in file("api"))
  .settings(name := "api")
  .enablePlugins(AkkaGrpcPlugin)
  .settings(protoSettings)
  .settings(Compile / PB.protoSources += file("api"))

lazy val bankingCtxDomain = (project in file("backend/banking/domain"))
  .settings(name := s"${baseName}-banking-domain")
  .settings(infrastructureSettings, coreSettings)

lazy val bankingCtxUsecase = (project in file("backend/banking/usecase"))
  .settings(name := s"${baseName}-banking-usecase")
  .settings(infrastructureSettings, coreSettings)
  .dependsOn(bankingCtxDomain)

lazy val bankingCtxAdapter = (project in file("backend/banking/adapter"))
  .settings(name := s"${baseName}-banking-adapter")
  .settings(infrastructureSettings, coreSettings)
  .dependsOn(api, bankingCtxUsecase)

lazy val banking = (project in file("backend/banking/cmd"))
  .settings(name := s"${baseName}-banking")
  .dependsOn(bankingCtxAdapter)

lazy val root = (project in file("backend"))
  .settings(name := s"${baseName}")
  .dependsOn(banking)