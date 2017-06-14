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
scalaVersion := "2.11.8"
crossScalaVersions := Seq("2.10.6", "2.11.8")
testGrouping in Test := singleTestPerJvm((definedTests in Test).value, (javaOptions in Test).value)
libraryDependencies ++=
  compileScope(kamonAkka, akkaDependency("actor").value, sprayCan, sprayClient, sprayRouting) ++
  providedScope(aspectJ) ++
  testScope(kamonTestkit, scalatest, akkaDependency("testkit").value, sprayTestkit)

import sbt.Tests._
def singleTestPerJvm(tests: Seq[TestDefinition], jvmSettings: Seq[String]): Seq[Group] =
  tests map { test =>
    Group(
      name = test.name,
      tests = Seq(test),
      runPolicy = SubProcess(ForkOptions(runJVMOptions = jvmSettings)))
  }
