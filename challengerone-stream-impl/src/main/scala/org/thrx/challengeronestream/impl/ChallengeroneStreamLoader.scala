package org.thrx.challengeronestream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import play.api.libs.ws.ahc.AhcWSComponents
import org.thrx.challengeronestream.api.ChallengeroneStreamService
import org.thrx.challengerone.api.ChallengeroneService
import com.softwaremill.macwire._

class ChallengeroneStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ChallengeroneStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ChallengeroneStreamApplication(context) with LagomDevModeComponents
}

abstract class ChallengeroneStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[ChallengeroneStreamService].to(wire[ChallengeroneStreamServiceImpl])
  )

  // Bind the ChallengeroneService client
  lazy val challengeroneService = serviceClient.implement[ChallengeroneService]
}
