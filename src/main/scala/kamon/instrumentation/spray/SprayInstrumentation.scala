/*
 * =========================================================================================
 * Copyright © 2013 the kamon project <http://kamon.io/>
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

package kamon.instrumentation.spray

import kamon.Kamon
import kamon.instrumentation.http.{HttpClientInstrumentation, HttpMessage, HttpServerInstrumentation}
import spray.http.HttpRequest

import scala.collection.concurrent.TrieMap

object SprayInstrumentation {

  Kamon.onReconfigure(_ => SprayInstrumentation.rebuildHttpClientInstrumentation(): Unit)

  private val _serverInstrumentations = TrieMap.empty[Int, HttpServerInstrumentation]
  @volatile var _httpClientInstrumentation: HttpClientInstrumentation = rebuildHttpClientInstrumentation

  def serverInstrumentation(interface: String, port: Int): HttpServerInstrumentation = {
    _serverInstrumentations.atomicGetOrElseUpdate(port, {
      HttpServerInstrumentation.from(Kamon.config().getConfig("kamon.instrumentation.spray.server"), "spray.can", interface, port)
    })
  }

  def clientInstrumentation: HttpClientInstrumentation =
    _httpClientInstrumentation

  private[spray] def rebuildHttpClientInstrumentation(): HttpClientInstrumentation = {
    val httpClientConfig = Kamon.config().getConfig("kamon.instrumentation.spray.client")
    _httpClientInstrumentation = HttpClientInstrumentation.from(httpClientConfig, "spray.client")
    _httpClientInstrumentation
  }

  private[spray] def toHttpRequest(request: HttpRequest): HttpMessage.Request = {
    new HttpMessage.Request {
      override def url: String = request.uri.toString()
      override def path: String = request.uri.path.toString()
      override def method: String = request.method.name
      override def host: String = request.uri.authority.host.address
      override def port: Int = request.uri.authority.port

      override def read(header: String): Option[String] =
        request.headers
          .find(_.is(header.toLowerCase))
          .map(_.value)

      override def readAll(): Map[String, String] =
        request.headers
          .map(h => (h.name, h.value))
          .toMap
    }
  }
}