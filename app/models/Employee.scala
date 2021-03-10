package models
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Employee(
                     _id:Option[BSONObjectID],
                     _joiningDate: Option[DateTime],
                     name:String,
                     address:String
                   )
object Employee{
  implicit val fmt : Format[Employee] = Json.format[Employee]
  implicit object EmployeeBSONReader extends BSONDocumentReader[Employee] {
    def read(doc: BSONDocument): Employee = {
      Employee(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONDateTime]("_joiningDate").map(dt => new DateTime(dt.value)),
        doc.getAs[String]("name").get,
        doc.getAs[String]("address").get)
    }
  }

  implicit object EmployeeBSONWriter extends BSONDocumentWriter[Employee] {
    def write(emp: Employee): BSONDocument = {
      BSONDocument(
        "_id" -> emp._id,
        "_joiningDate" -> emp._joiningDate.map(date => BSONDateTime(date.getMillis)),
        "name" -> emp.name,
        "address" -> emp.address

      )
    }
  }
}

