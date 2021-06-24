package kamon.instrumentation.spray;

import kanela.agent.libs.net.bytebuddy.asm.Advice;
import scala.Function1;
import scala.concurrent.Future;
import spray.http.HttpRequest;
import spray.http.HttpResponse;

public class RequestLevelApiSendReceiveAdvice {

    @Advice.OnMethodExit
    public static void wrapSendReceive(@Advice.Return(readOnly = false) Function1<HttpRequest, Future<HttpResponse>> sendReceive) {
        sendReceive = ClientInstrumentation.wrapSendReceive(sendReceive);
    }
}
