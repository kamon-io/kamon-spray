/* =========================================================================================
 * Copyright © 2013-2016 the kamon project <http://kamon.io/>
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

import sbt._

object Dependencies {

  val resolutionRepos = Seq(
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
    "Kamon Repository Snapshots" at "http://snapshots.kamon.io"
  )

  val kamonAkkaVersion  = "0.6.6"
  val kamonCoreVersion  = "0.6.5"
  val aspectjVersion    = "1.8.10"
  val sprayVersion      = "1.3.4"
  val akkaVersion       = "2.3.16"

  val aspectJ           = "org.aspectj"               %   "aspectjweaver"         % aspectjVersion

  val kamonAkka         = "io.kamon"                  %%  "kamon-akka-2.3"        % kamonAkkaVersion
  val kamonTestkit      = "io.kamon"                  %%  "kamon-testkit"         % kamonCoreVersion

  val sprayCan          = "io.spray"                  %%  "spray-can"             % sprayVersion
  val sprayRouting      = "io.spray"                  %%  "spray-routing"         % sprayVersion
  val sprayTestkit      = "io.spray"                  %%  "spray-testkit"         % sprayVersion
  val sprayClient       = "io.spray"                  %%  "spray-client"          % sprayVersion

  val akkaActor         = "com.typesafe.akka"         %%  "akka-actor"            % akkaVersion
  val akkaSlf4j         = "com.typesafe.akka"         %%  "akka-slf4j"            % akkaVersion
  val akkaTestKit       = "com.typesafe.akka"         %%  "akka-testkit"          % akkaVersion

  val scalatest         = "org.scalatest"             %%  "scalatest"             % "3.0.1"
  val logback           = "ch.qos.logback"            %   "logback-classic"       % "1.0.13"

  def compileScope   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def providedScope  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def testScope      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
}
