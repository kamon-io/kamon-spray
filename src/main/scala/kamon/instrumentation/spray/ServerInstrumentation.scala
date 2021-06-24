package kamon.instrumentation.spray

import kamon.Kamon
import kamon.context.Context
import kamon.instrumentation.http.HttpServerInstrumentation
import kanela.agent.api.instrumentation.InstrumentationBuilder
import kanela.agent.libs.net.bytebuddy.asm.Advice
import spray.http.{HttpMessagePartWrapper, HttpRequest, HttpResponse}


class ServerInstrumentation extends InstrumentationBuilder {

  onSubTypesOf("spray.can.server.OpenRequestComponent$DefaultOpenRequest")
    .mixin(classOf[HasServerRequestHandler.Mixin])
    .advise(isConstructor, classOf[DefaultOpenRequestInitAdvice])
    .advise(method("handleResponseEndAndReturnNextOpenRequest"), classOf[HandleResponseAdvice])

  onType("spray.can.server.ServerFrontend$$anon$2$$anon$1")
    .advise(method("spray$can$server$ServerFrontend$$anon$$anon$$openNewRequest"), classOf[OpenNewRequestAdvice])
}

trait HasServerRequestHandler {
  def requestHandler(): HttpServerInstrumentation.RequestHandler
  def setRequestHandler(requestHandler: HttpServerInstrumentation.RequestHandler): Unit
}

object HasServerRequestHandler {
  class Mixin(@transient private var _requestHandler: HttpServerInstrumentation.RequestHandler) extends HasServerRequestHandler {

    override def requestHandler(): HttpServerInstrumentation.RequestHandler =
      _requestHandler

    override def setRequestHandler(requestHandler: HttpServerInstrumentation.RequestHandler): Unit =
      _requestHandler = requestHandler
  }
}

class DefaultOpenRequestInitAdvice
object DefaultOpenRequestInitAdvice {

  @Advice.OnMethodExit
  def exit(@Advice.This openRequest: HasServerRequestHandler, @Advice.Argument(1) request: HttpRequest): Unit = {
    val authority = request.uri.authority
    val serverInstrumentation = SprayInstrumentation.serverInstrumentation(authority.host.address, authority.port)
    val handler = serverInstrumentation.createHandler(SprayInstrumentation.toHttpRequest(request))

    openRequest.setRequestHandler(handler)
    Kamon.storeContext(handler.context)
  }
}

class OpenNewRequestAdvice
object OpenNewRequestAdvice {

  @Advice.OnMethodExit
  def exit(): Unit = {
    // Clears the context stored in DefaultOpenRequestInitAdvice.
    Kamon.storeContext(Context.Empty)
  }
}

class HandleResponseAdvice
object HandleResponseAdvice {
  import kamon.instrumentation.http.HttpMessage

  @Advice.OnMethodExit
  def exit(@Advice.This openRequest: HasServerRequestHandler, @Advice.Argument(0) response: HttpMessagePartWrapper): Unit = {
    response match {
      case httpResponse: HttpResponse =>
        val requestHandler = openRequest.requestHandler()
        requestHandler.buildResponse(noopResponseBuilder(httpResponse), requestHandler.context)
        requestHandler.responseSent()
      case _ =>
        openRequest.requestHandler().responseSent()
    }
  }

  // For now, we are not bringing support for writing the trace ID in response headers.
  def noopResponseBuilder(response: HttpResponse): HttpMessage.ResponseBuilder[HttpMessagePartWrapper] =
    new HttpMessage.ResponseBuilder[HttpMessagePartWrapper] {
      override def statusCode: Int = response.status.intValue
      override def build(): HttpMessagePartWrapper = response
      override def write(header: String, value: String): Unit = {}
    }
}