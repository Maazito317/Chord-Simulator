name := "CourseProject"

version := "1.0"

scalaVersion := "2.13.1"
mainClass in (Compile, run) := Some("CourseProject.Main")
trapExit := false

lazy val akkaHttpVersion = "10.1.10"
lazy val akkaVersion    = "2.6.0"
lazy val scalatestVersion = "3.0.7"


libraryDependencies += "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream"              % akkaVersion
libraryDependencies += "ch.qos.logback"    % "logback-classic"           % "1.2.3"
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test
libraryDependencies += "org.scalatest"     %% "scalatest"                % "3.0.8"         % Test
libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.10"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.3.0-alpha4"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.3.0-alpha4" % Test
libraryDependencies += "ch.qos.logback" % "logback-examples" % "1.3.0-alpha4"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.8.0-beta1"
//libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test->default"
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-api" % "5.5.2" % Test
