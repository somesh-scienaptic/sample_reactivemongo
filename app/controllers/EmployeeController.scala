package controllers
import javax.inject._
import play.api.mvc._
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.{Json, __}
import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext
import repository.EmployeeRepository
import models.Employee
import play.api.libs.json.JsValue

@Singleton
class EmployeeController @Inject()(
                                 implicit executionContext: ExecutionContext,
                                 val empRepository: EmployeeRepository,
                                 val controllerComponents: ControllerComponents)
  extends BaseController {
  // controller actions goes here...
  def findAll():Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    empRepository.findAll().map {
      emp => Ok(Json.toJson(emp))
    }
  }

  def findOne(id:String):Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val objectIdTryResult = BSONObjectID.parse(id)
    objectIdTryResult match {
      case Success(objectId) => empRepository.findOne(objectId).map {
        movie => Ok(Json.toJson(movie))
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the employee id"))
    }
  }

  def create():Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request => {

    request.body.validate[Employee].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      movie =>
        empRepository.create(movie).map {
          _ => Created(Json.toJson(movie))
        }
    )
  }}

  def update(
              id: String):Action[JsValue]  = Action.async(controllerComponents.parsers.json) { implicit request => {
    request.body.validate[Employee].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      movie =>{
        val objectIdTryResult = BSONObjectID.parse(id)
        objectIdTryResult match {
          case Success(objectId) => empRepository.update(objectId, movie).map {
            result => Ok(Json.toJson(result.ok))
          }
          case Failure(_) => Future.successful(BadRequest("Cannot parse the employee id"))
        }
      }
    )
  }}

  def delete(id: String):Action[AnyContent]  = Action.async { implicit request => {
    val objectIdTryResult = BSONObjectID.parse(id)
    objectIdTryResult match {
      case Success(objectId) => empRepository.delete(objectId).map {
        _ => NoContent
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the employee id"))
    }
  }}


}