import sbt._

object GoogleApiGrpc {
  val protoGoogleCommonProtos = "com.google.api.grpc" % "proto-google-common-protos" % "2.7.4"
}

object Grpc {
  val grpcProtobuf    = "io.grpc" % "grpc-protobuf"     % "1.45.0"
  val grpcNettyShaded = "io.grpc" % "grpc-netty-shaded" % "1.45.0"
}

object TypesafeAkka {
  val akkaActorTyped        = "com.typesafe.akka" %% "akka-actor-typed"         % "2.6.18"
  val akkaStream            = "com.typesafe.akka" %% "akka-stream"              % "2.6.18"
  val akkaDiscovery         = "com.typesafe.akka" %% "akka-discovery"           % "2.6.18"
  val akkaPki               = "com.typesafe.akka" %% "akka-pki"                 % "2.6.18"
  val akkaActorTestkitTyped = "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.18"
  val akkaStreamTestkit     = "com.typesafe.akka" %% "akka-stream-testkit"      % "2.6.18"
  val akkaHttp              = "com.typesafe.akka" %% "akka-http"                % "10.2.9"
  val akkaHttpCore          = "com.typesafe.akka" %% "akka-http-core"           % "10.2.9"
  val akkaHttp2Support      = "com.typesafe.akka" %% "akka-http2-support"       % "10.2.9"
}

object Atnos {
  val eff = "org.atnos" %% "eff" % "5.23.0"
}

object Scalatest {
  val scalatest = "org.scalatest" %% "scalatest" % "3.2.11"
}
