package com.example.auction.user.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import com.example.auction.utils.JsonFormats._

trait UserService extends Service {
  def createUser: ServiceCall[User, User]
  def getUser(userId: UUID): ServiceCall[NotUsed, User]

  // Remove once we have a proper user service
  // FIXME THR
  def getUsers: ServiceCall[NotUsed, Seq[User]]

  def descriptor = {
    import Service._
    named("user").withCalls(
      pathCall("/api/user", createUser),
      pathCall("/api/user/:id", getUser _),
      pathCall("/api/user", getUsers)
    )
  }
}

/**
 * User
 * Der User ist der Teilnehmer an der C.one Plattform. 
 * Der User benötigt dabei ein Account welcher durch eine erfolgreiche Registrierung bekommt. 
 * Diese Registrierung kann der User beim Start der App/Portal anlegen.
 * 
 */
case class User(
    id: Option[UUID],
    nickName: String,
    givenName: String, 
    familyname: String,
    postCode: String, // PLZ 
    eMail: String,
    mobilPhone: Option[String], 
    street: Option[String], 
    city: Option[String] 
    ) {
      def safeId = id.getOrElse(UUID.randomUUID())
    }


object User {
  implicit val format: Format[User] = Json.format

  def create(
    nickName: String,
    givenName: String, 
    familyname: String,
    postCode: String, // PLZ 
    eMail: String,
    mobilPhone: Option[String],
    street: Option[String], 
    city: Option[String] 
    ) = User(None, nickName, givenName, familyname, postCode, eMail, mobilPhone, street, city)
}


