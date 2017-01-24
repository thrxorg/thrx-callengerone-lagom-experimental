package com.example.auction.user.impl

import java.util.UUID

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.example.auction.user.api
import com.example.auction.user.api.UserService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.example.auction.security.ServerSecurity._

import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.server.ServerServiceCall

class UserServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) extends UserService {

  private val currentIdsQuery = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def createUser = ServiceCall { createUser =>
    val userId = UUID.randomUUID()
    val pUser = User(userId, createUser.nickName, createUser.givenName, createUser.familyname, createUser.postCode, createUser.eMail, createUser.mobilPhone, createUser.street, createUser.city)
    entityRef(userId).ask(CreateUser(pUser)).map { _ => convertUser(pUser) }
  }

  private def convertUser(user: User): api.User = {
    api.User(Some(user.id), user.nickName, user.givenName, user.familyname, user.postCode, user.eMail, user.mobilPhone, user.street, user.city)
  }
  
  override def getUser(userId: UUID) = ServiceCall { _ =>
    entityRef(userId).ask(GetUser).map {
      case Some(user) => convertUser(user)
      case None => throw NotFound(s"User with id $userId")
    }
  }

  // FIXME THR 
  override def getUsers = ServiceCall { _ =>
    // Note this should never make production....
    currentIdsQuery.currentPersistenceIds()
      .filter(_.startsWith("UserEntity|"))
      .mapAsync(4) { id =>
        val entityId = id.split("\\|", 2).last
        registry.refFor[UserEntity](entityId)
          .ask(GetUser)
          .map(_.map(user => convertUser(user))) // FIXME THR kaput gemacht ???
      }.collect {
        case Some(user) => user
      }
      .runWith(Sink.seq)
  }

  private def entityRef(userId: UUID) = registry.refFor[UserEntity](userId.toString)
  
//  private def entityRef(userId: UUID) = entityRefString(userId.toString)
//  private def entityRefString(userId: String) = registry.refFor[UserEntity](userId)

}
