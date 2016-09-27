package models

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import slick.dbio
import slick.dbio.Effect.Read
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.sys.process._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration
import scala.util.{Failure, Success}
import scala.concurrent.duration.Duration
import play.api.cache._
import java.util.Date

case class Image(pIndex:Int, imgName:String, jobName:String, jobType:String, parentInfo:String, status:String, date:String, uId:String)

class ImageModel @Inject()(protected val dbConfigProvider: DatabaseConfigProvider){
  
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  
  import dbConfig.driver.api._

  class ImageTable(tag: Tag) extends Table[Image](tag, "Image") {

    def pIndex = column[Int]("pIndex", O.AutoInc, O.PrimaryKey)
    def imgName = column[String]("imgName")
    def jobName = column[String]("jobName")
    def jobType = column[String]("jobType")
    def parentInfo = column[String]("parentInfo")
    def status = column[String]("status")
    def date = column[String]("date")
    def uId = column[String]("uId")

//    implicit val JavaUtilDateMapper = MappedColumnType .base[java.util.Date, java.sql.Timestamp] (d => new java.sql.Timestamp(d.getTime),d => new java.util.Date(d.getTime))
    
    implicit val utilDate2SqlDate = MappedColumnType.base[java.util.Date, java.sql.Date](
{ utilDate => new java.sql.Date(utilDate.getTime()) },
{ sqlDate => new java.util.Date(sqlDate.getTime()) })
    
    
    def * = (pIndex, imgName, jobName, jobType, parentInfo, status, date ,uId) <> (Image.tupled, Image.unapply)
  }
    val Images = TableQuery[ImageTable]
  
    def insert(imageData:Image)=db.run(DBIO.seq( Images += imageData ))

   def update(jobName: String, status:String)={
      val q = for { image <- Images if image.jobName === jobName } yield image.status
      val updateAction = db.run(q.update(status));
    }
   
   def delete(pIndex: Int)= db.run(Images.filter{_.pIndex === pIndex}.result)
    
    def retrieve(data: String,flag: Int):Seq[Image]= {
     if(flag==1){
      val dbRetrieve = db.run(compiledCheckByName(data).result)
      val result =Await.result(dbRetrieve, Duration.Inf)
       return result
     }
     else if(flag==2){
      val dbRetrieve = db.run(compiledCheckById(data).result)
      val result =Await.result(dbRetrieve, Duration.Inf)
      return result
     }
     else if(flag==3){
      val dbRetrieve = db.run(compiledCheckByStatus(data).result)
      val result =Await.result(dbRetrieve, Duration.Inf)
        return result
     }
     else{
       return null;
     }
    }
   

       
    def checkByName(jobName:Rep[String]) = Images.filter{image => image.jobName === jobName}
    def compiledCheckByName = Compiled(checkByName _)
    def checkById(uId:Rep[String]) = Images.filter{image => image.uId === uId}
    def compiledCheckById = Compiled(checkById _)
    def checkByStatus(status:Rep[String]) = Images.filter{image => image.status === status}
    def compiledCheckByStatus = Compiled(checkByStatus _)
}