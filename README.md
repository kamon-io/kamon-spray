Spray Integration   ![Build Status](https://travis-ci.org/kamon-io/kamon-spray.svg?branch=master)
==========================

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kamon-io/Kamon?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

***kamon-spray*** [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kamon/kamon-play-25_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.kamon/kamon-spray_2.11)

Spray Integration
=================

The `kamon-spray` module brings bytecode instrumentation and tools that provide what we consider base functionality for
a supported HTTP toolkit.

The <b>kamon-spray</b> module requires you to start your application using the AspectJ Weaver Agent. Kamon will warn you
at startup if you failed to do so.


Server Side Tools
-----------------

One of the main goals in our Spray integration is to measure the behavior of your HTTP server and for that, we
automatically start and finish traces for you, but there is one caveat here, though. When you start traces with Kamon
you need to provide a name for it and that name will be used for metric tracking purposes; it is really important that
you give a meaningful name to your traces, but our integration the traces are not started by a piece of code that you
can control but rather by instrumentation injected in Spray's internals, so you need to ensure that a proper name is
assigned to the traces for them to be really useful.


Client Side Tools
-----------------

If you are using `spray-client` to send HTTP requests to other services then we also have something to offer on this
side. The bytecode instrumentation provided by the `kamon-spray` module hooks into Spray's internals to automatically
start and finish segments for requests that are issued within a trace. This translates into you having metrics about how
the services you are calling are behaving.

As you might already know, `spray-client` comes with three different levels of abstraction that can be used to issue
HTTP requests, namely the Request-level API, the Host-level API and the Connection-level API. If you are using any of
the first two options, then our instrumentation can automatically create and finish segments for you, whereas if you are
using the Connection-level API you will need to manage segments on your own.

Since each of the client API levels provided in Spray builds on top of the previous level, you will beed to use the
`kamon.spray.client.instrumentation-level` configuration key to tell Kamon at which level you want the segments to be
measured. The available options are:

* __request-level__: measures the time during which the user application code is waiting for a `spray-client` request to
complete, by attaching a callback to the Future[HttpResponse] returned by `spray.client.pipelining.sendReceive`.
If `spray.client.pipelining.sendReceive` is not used, the segment measurement wont be performed.

* __host-level__: measures the internal time taken by spray-client to finish a request. Sometimes the user application
code has a finite future timeout (like when using `spray.client.pipelining.sendReceive`) that doesn't match
the actual amount of time spray might take internally to resolve a request, counting retries, redirects,
connection timeouts and so on. If using the host level instrumentation, the measured time will include the entire time
since the request has been received by the corresponding `HttpHostConnector` until a response is sent back
to the requester.

