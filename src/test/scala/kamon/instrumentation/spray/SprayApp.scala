package kamon.instrumentation.spray

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import kamon.Kamon
import spray.can.Http
import spray.http.{HttpMethods, HttpRequest, HttpResponse, Uri}
import spray.routing.SimpleRoutingApp

import scala.concurrent.duration.DurationInt

object SprayApp extends App with SimpleRoutingApp {
  Kamon.init()

  implicit val system = ActorSystem("my-system")
  implicit val timeout: Timeout = Timeout(15.seconds)
  import system.dispatcher

  startServer(interface = "localhost", port = 8080) {
    path("hello") {
      get {
        complete {
          val response = (IO(Http) ? HttpRequest(HttpMethods.GET, Uri("http://spray.io")))
            .mapTo[HttpResponse]
            .onComplete(t => println("Terminated the request"))

          <h1>Say hello to spray</h1>
        }
      }
    }
  }
}