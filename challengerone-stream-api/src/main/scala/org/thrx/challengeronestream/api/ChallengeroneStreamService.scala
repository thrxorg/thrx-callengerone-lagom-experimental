package org.thrx.challengeronestream.api

import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The ChallengerOne stream interface.
  *
  * This describes everything that Lagom needs to know about how to serve and
  * consume the ChallengeroneStream service.
  */
trait ChallengeroneStreamService extends Service {

  def stream: ServiceCall[Source[String, _], Source[String, _]]

  override final def descriptor = {
    import Service._

    named("challengerone-stream")
      .withCalls(
        namedCall("stream", stream)
      ).withAutoAcl(true)
  }
}

