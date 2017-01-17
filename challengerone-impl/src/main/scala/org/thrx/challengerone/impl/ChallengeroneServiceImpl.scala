package org.thrx.challengerone.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.thrx.challengerone.api.ChallengeroneService

/**
  * Implementation of the ChallengeroneService.
  */
class ChallengeroneServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends ChallengeroneService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the ChallengerOne entity for the given ID.
    val ref = persistentEntityRegistry.refFor[ChallengeroneEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id, None))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the ChallengerOne entity for the given ID.
    val ref = persistentEntityRegistry.refFor[ChallengeroneEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }
}
