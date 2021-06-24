/* =========================================================================================
 * Copyright © 2013-2017 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */


 val kamonCore          = "io.kamon"                  %% "kamon-core"             % "0.6.7"
 val kamonAkka          = "io.kamon"                  %% "kamon-akka-2.3"         % "0.6.7"
 val kamonTestkit       = "io.kamon"                  %% "kamon-testkit"          % "0.6.7"

 val sprayCan           = "io.spray"                  %%  "spray-can"             % "1.3.3"
 val sprayRouting       = "io.spray"                  %%  "spray-routing"         % "1.3.3"
 val sprayTestkit       = "io.spray"                  %%  "spray-testkit"         % "1.3.3"
 val sprayClient        = "io.spray"                  %%  "spray-client"          % "1.3.3"

name := "kamon-spray"
scalaVersion := "2.11.12"
crossScalaVersions := Seq("2.11.12")
libraryDependencies ++= Seq(
  sprayCan, sprayRouting, sprayClient,
  "io.kamon"% "kanela-agent" % "1.0.9" % "provided",
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.17",
  "io.kamon"          %% "kamon-bundle"           % "2.2.0",
  "io.kamon"          %% "kamon-testkit"          % "2.2.0",
  "io.kamon"          %% "kamon-apm-reporter"          % "2.2.0",
  scalatest,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.17",
sprayTestkit
)
