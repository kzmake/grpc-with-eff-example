addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc"       % "2.1.3")
addSbtPlugin("com.lightbend.sbt"       % "sbt-javaagent"       % "0.1.5")
addSbtPlugin("org.scalameta"           % "sbt-scalafmt"        % "2.4.3")
addSbtPlugin("ch.epfl.scala"           % "sbt-scalafix"        % "0.9.31")
addSbtPlugin("com.github.sbt"          % "sbt-native-packager" % "1.9.4")

// https://scalapb.github.io/docs/validation/#installation
libraryDependencies ++= Seq("com.thesamet.scalapb" %% "scalapb-validate-codegen" % "0.3.2")
