package kamon.instrumentation.spray

import kamon.Kamon
import kanela.agent.api.instrumentation.InstrumentationBuilder
import spray.http.{HttpRequest, HttpResponse}
import kamon.instrumentation.http.HttpMessage
import kamon.util.CallingThreadExecutionContext
import spray.http.HttpHeaders.RawHeader

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ClientInstrumentation extends InstrumentationBuilder {

  onSubTypesOf("spray.client.pipelining$")
    .advise(method("sendReceive"), classOf[RequestLevelApiSendReceiveAdvice])
}

object ClientInstrumentation {

  def wrapSendReceive(originalSendReceive: HttpRequest => Future[HttpResponse]): HttpRequest => Future[HttpResponse] = {
    val clientInstrumentation = SprayInstrumentation.clientInstrumentation


    (request: HttpRequest) ⇒ {
      val clientHandler = clientInstrumentation.createHandler(toRequestBuilder(request), Kamon.currentContext())
      val responseFuture = originalSendReceive.apply(clientHandler.request)

      responseFuture.onComplete {
        case Success(response) ⇒ clientHandler.processResponse(toResponseMessage(response))
        case Failure(t) ⇒ clientHandler.span.fail(t).finish()
      }(CallingThreadExecutionContext)

      responseFuture
    }
  }

  def toRequestBuilder(request: HttpRequest): HttpMessage.RequestBuilder[HttpRequest] =
    new HttpMessage.RequestBuilder[HttpRequest] {
      private var _newHeaders = Map.empty[String, String]
      override def build(): HttpRequest = {
        val allHeaders = request.headers ++ _newHeaders.map {
          case (name, value) => RawHeader(name, value)
        }

        request.withHeaders(allHeaders)
      }

      override def write(header: String, value: String): Unit =
        _newHeaders = _newHeaders.updated(header, value)

      override def url: String = request.uri.toString()
      override def path: String = request.uri.path.toString()
      override def method: String = request.method.name
      override def host: String = request.uri.authority.host.address
      override def port: Int = request.uri.authority.port
      override def read(header: String): Option[String] = request.headers.find(_.name == header).map(_.value)
      override def readAll(): Map[String, String] = request.headers.map(h => (h.name, h.value)).toMap
    }

  def toResponseMessage(response: HttpResponse): HttpMessage.Response = new HttpMessage.Response {
    override def statusCode: Int = response.status.intValue
  }




}

