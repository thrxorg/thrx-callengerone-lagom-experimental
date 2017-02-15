package controllers

import java.util.{Locale, UUID}

import com.example.auction.item.api.ItemStatus
import com.example.auction.user.api.{User, UserService}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.Action

import scala.concurrent.ExecutionContext
import scala.util.Try

class Main(messagesApi: MessagesApi, userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(messagesApi, userService) {

  val form = Form(mapping(
      "id" -> optional(
        text.verifying("invalid.id", id => Try(UUID.fromString(id)).isSuccess)
          .transform[UUID](UUID.fromString, _.toString)
      ),
    "nickName" -> nonEmptyText,
    "givenName" -> nonEmptyText, 
    "familyname" -> nonEmptyText,
    "postCode" -> nonEmptyText,
    "eMail" -> nonEmptyText,
    "mobilPhone" -> optional(text),
    "street" -> optional(text),
    "city" -> optional(text) 
  )(UserForm.apply)(UserForm.unapply))

  def index = Action.async { implicit rh =>
    withUser(loadNav(_).map { implicit nav =>
      Ok(views.html.index())
    })
  }

  def createUserForm = Action.async { implicit rh =>
    withUser(loadNav(_).map { implicit nav =>
      Ok(views.html.createUser(form))
    })
  }

  def createUser = Action.async { implicit rh =>
    form.bindFromRequest().fold(
      errorForm => {
        withUser(loadNav(_).map { implicit nav =>
          Ok(views.html.createUser(errorForm))
        })
      },
      createUserForm => {
        userService.createUser.invoke(
            User(
                createUserForm.id,
                createUserForm.nickName,
                createUserForm.givenName,
                createUserForm.familyname,
                createUserForm.postCode,
                createUserForm.eMail,
                createUserForm.mobilPhone,
                createUserForm.street,
                createUserForm.city
                )
            ).map { user =>
          Redirect(routes.ProfileController.myItems(ItemStatus.Completed.toString.toLowerCase(Locale.ENGLISH), None, None))
            .withSession("user" -> user.id.toString)
        }
      }
    )
  }

  def currentUser(userId: UUID) = Action { rh =>
    Ok.withSession("user" -> userId.toString)
  }
}

case class UserForm(
    id: Option[UUID] = None,
    nickName: String,
    givenName: String, 
    familyname: String,
    postCode: String, // PLZ 
    eMail: String,
    mobilPhone: Option[String], 
    street: Option[String], 
    city: Option[String] 
    )
