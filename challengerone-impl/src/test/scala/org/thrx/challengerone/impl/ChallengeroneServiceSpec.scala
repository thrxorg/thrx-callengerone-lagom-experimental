package org.thrx.challengerone.impl

import java.io.File

import akka.cluster.Cluster
import akka.persistence.cassandra.testkit.CassandraLauncher
import com.lightbend.lagom.scaladsl.persistence.cassandra.testkit.TestUtil
import com.lightbend.lagom.scaladsl.server.{LagomApplicationContext, LocalServiceLocator}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import play.api.Configuration
import play.core.server.NettyServer
import org.thrx.challengerone.api._

import scala.concurrent.Promise

class ChallengeroneServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  CassandraLauncher.start(new File("target/ChallengeroneServiceSpec"), CassandraLauncher.DefaultTestConfigResource,
    clean = true, port = 0)

  private val port = Promise[Int]()
  private val app = new ChallengeroneApplication(LagomApplicationContext.Test) with LocalServiceLocator {
    override def lagomServicePort = port.future
    override def additionalConfiguration: Configuration = Configuration(TestUtil.persistenceConfig(
      "ChallengeroneServiceSpec",
      CassandraLauncher.randomPort
    ))
  }
  val server = NettyServer.fromApplication(app.application)
  port.success(server.httpPort.get)
  val client = app.serviceClient.implement[ChallengeroneService]

  // Start the cluster
  val cluster = Cluster(app.actorSystem)
  cluster.join(cluster.selfAddress)

  override protected def afterAll(): Unit = {
    server.stop()
    CassandraLauncher.stop()
  }

  "ChallengerOne service" should {

    "say hello" in {
      client.hello("Alice").invoke().map { answer =>
        answer should ===("Hello, Alice!")
      }
    }

    "allow responding with a custom message" in {
      for {
        _ <- client.useGreeting("Bob").invoke(GreetingMessage("Hi"))
        answer <- client.hello("Bob").invoke()
      } yield {
        answer should ===("Hi, Bob!")
      }
    }
  }
}
