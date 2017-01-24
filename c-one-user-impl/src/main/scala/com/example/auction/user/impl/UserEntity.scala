package com.example.auction.user.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{Jsonable, SerializerRegistry, Serializers}
import play.api.libs.json.{Format, Json}
import com.example.auction.utils.JsonFormats._
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import java.util.UUID

class UserEntity extends PersistentEntity {
  override type Command = UserCommand
  override type Event = UserEvent
  override type State = Option[User]
  override def initialState = None

  override def behavior: Behavior = {
    case Some(user) =>
      Actions().onReadOnlyCommand[GetUser.type, Option[User]] {
        case (GetUser, ctx, state) => ctx.reply(state)
      }.onReadOnlyCommand[CreateUser, Done] {
        case (CreateUser(name), ctx, state) => ctx.invalidCommand("User already exists")
      }
    case None =>
      Actions().onReadOnlyCommand[GetUser.type, Option[User]] {
        case (GetUser, ctx, state) => ctx.reply(state)
      }.onCommand[CreateUser, Done] {
        case (CreateUser(user), ctx, state) =>
          ctx.thenPersist(UserCreated(user), _ => ctx.reply(Done))
      }.onEvent {
        case (UserCreated(user), state) => Some(user)
      }
  }
}

case class User(
    id: UUID,
    nickName: String,
    givenName: String, 
    familyname: String,
    postCode: String, // PLZ 
    eMail: String,
    mobilPhone: Option[String], 
    street: Option[String], 
    city: Option[String] 
    )

object User {
  implicit val format: Format[User] = Json.format
}

sealed trait UserEvent extends Jsonable

case class UserCreated(user: User) extends UserEvent

object UserCreated {
  implicit val format: Format[UserCreated] = Json.format
}

sealed trait UserCommand extends Jsonable

case class CreateUser(user: User) extends UserCommand with ReplyType[Done]

object CreateUser {
  implicit val format: Format[CreateUser] = Json.format
}

case object GetUser extends UserCommand with ReplyType[Option[User]] {
  implicit val format: Format[GetUser.type] = singletonFormat(GetUser)
}

class UserSerializerRegistry extends SerializerRegistry {
  override def serializers = List(
    Serializers[User],
    Serializers[UserCreated],
    Serializers[CreateUser],
    Serializers[GetUser.type]
  )
}