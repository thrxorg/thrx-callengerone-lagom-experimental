package org.thrx.challengeronestream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.thrx.challengeronestream.api.ChallengeroneStreamService
import org.thrx.challengerone.api.ChallengeroneService

import scala.concurrent.Future

/**
  * Implementation of the ChallengeroneStreamService.
  */
class ChallengeroneStreamServiceImpl(challengeroneService: ChallengeroneService) extends ChallengeroneStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(challengeroneService.hello(_).invoke()))
  }
}
