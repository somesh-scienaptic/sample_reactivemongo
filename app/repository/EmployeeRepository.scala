package repository
import javax.inject._
import reactivemongo.api.bson.collection.BSONCollection
import play.modules.reactivemongo.ReactiveMongoApi
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import org.joda.time.DateTime
import reactivemongo.api.commands.WriteResult
import models.Employee

@Singleton
class EmployeeRepository @Inject()(
                                    implicit executionContext: ExecutionContext,
                                    reactiveMongoApi: ReactiveMongoApi
                                  ) {
  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(db => db.collection("employee"))
  // Repository methods ...

  def findAll(limit: Int = 100): Future[Seq[Employee]] = {

    collection.flatMap(
      _.find(BSONDocument(), Option.empty[Employee])
        .cursor[Employee](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Employee]]())
    )
  }

  def findOne(id: BSONObjectID): Future[Option[Employee]] = {
    collection.flatMap(_.find(BSONDocument("_id" -> id), Option.empty[Employee]).one[Employee])
  }

  def create(emp: Employee): Future[WriteResult] = {
    collection.flatMap(_.insert(ordered = false)
      .one(emp.copy(_joiningDate = Some(new DateTime()))))
  }

  def update(id: BSONObjectID, emp: Employee):Future[WriteResult] = {

    collection.flatMap(
      _.update(ordered = false).one(BSONDocument("_id" -> id),
        emp.copy(
          _joiningDate = Some(new DateTime())))
    )
  }

  def delete(id: BSONObjectID):Future[WriteResult] = {
    collection.flatMap(
      _.delete().one(BSONDocument("_id" -> id), Some(1))
    )
  }
}
