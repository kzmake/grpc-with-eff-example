import sbt._

object GoogleApiGrpc {
  val version = "2.7.4"
  val common  = "com.google.api.grpc" % "proto-google-common-protos" % version
}

object GrpcProtobuf {
  val version     = "1.43.2"
  val common      = "io.grpc" % "grpc-protobuf"     % version
  val nettyShaded = "io.grpc" % "grpc-netty-shaded" % version
}

object Akka {
  val version           = "2.6.18"
  val actorTyped        = "com.typesafe.akka" %% "akka-actor-typed"         % version
  val stream            = "com.typesafe.akka" %% "akka-stream"              % version
  val discovery         = "com.typesafe.akka" %% "akka-discovery"           % version
  val pki               = "com.typesafe.akka" %% "akka-pki"                 % version
  val actorTestkitTyped = "com.typesafe.akka" %% "akka-actor-testkit-typed" % version
  val streamTestkit     = "com.typesafe.akka" %% "akka-stream-testkit"      % version
}

object AkkaHttp {
  val version      = "10.2.7"
  val http         = "com.typesafe.akka" %% "akka-http"          % version
  val core         = "com.typesafe.akka" %% "akka-http-core"     % version
  val http2Support = "com.typesafe.akka" %% "akka-http2-support" % version
}

object Eff {
  val version = "5.23.0"
  val core    = "org.atnos" %% "eff" % version
}

object Logback {
  val version = "1.2.11"
  val classic = "ch.qos.logback" % "logback-classic" % version
}

object ScalaLogging {
  val version = "3.9.4"
  val core    = "com.typesafe.scala-logging" %% "scala-logging" % version
}

object Logstash {
  val version        = "7.0.1"
  val logbackEncoder = "net.logstash.logback" % "logstash-logback-encoder" % version
}

object Jackson {
  val version = "2.13.2"
  val scala   = "com.fasterxml.jackson.module" %% "jackson-module-scala" % version
}

object ScalaTest {
  val version = "3.2.11"
  val core    = "org.scalatest" %% "scalatest" % version
}
