package org.thrx.challengerone.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import play.api.libs.ws.ahc.AhcWSComponents
import org.thrx.challengerone.api.ChallengeroneService
import com.softwaremill.macwire._

class ChallengeroneLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ChallengeroneApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ChallengeroneApplication(context) with LagomDevModeComponents
}

abstract class ChallengeroneApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[ChallengeroneService].to(wire[ChallengeroneServiceImpl])
  )

  // Register the ChallengerOne persistent entity
  persistentEntityRegistry.register(wire[ChallengeroneEntity])
}
