package controllers

import play.api.mvc._
import play.api._
import play.api.Logger
import play.api.i18n._
import javax.inject.Inject
import models._
import play.api.data._
import play.api.data.Forms._
import scala.sys.process._
import java.util.Date
import java.io._
import java.text.SimpleDateFormat
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import java.io.{ FileWriter, FileOutputStream, File }
import controllers._
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{ Files, Path }
import java.nio.file.Paths
import java.util
import akka.stream.IOResult
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.actor._
import akka.stream.Materializer

import play.api.libs.streams._
import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.streams.Accumulator
import play.core.parsers.Multipart.FileInfo
import scala.concurrent.Future
import java.io.ByteArrayOutputStream
import play.api.libs.iteratee.Iteratee
import play.api.mvc.{ BodyParser, MultipartFormData }
import scala.concurrent.ExecutionContext.Implicits.global
import play.core.parsers.Multipart
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import akka.util.ByteString
import java.nio.file.attribute.BasicFileAttributes
import akka.stream.scaladsl.{ FileIO, Sink }
import akka.stream.scaladsl.FileIO
import java.nio.file.StandardOpenOption
import akka.stream.SinkShape
import akka.NotUsed
import reflect.io._
import play.api.libs.json._

import slick.dbio
import slick.dbio.Effect.Read
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.sys.process._
import scala.concurrent.Await
import scala.concurrent.duration
import scala.util.{ Failure, Success }
import scala.concurrent.duration.Duration
import play.api.cache._
import play.api.libs.json._


class LaunchController @Inject() (implicit system: ActorSystem, materializer: Materializer, val messagesApi: MessagesApi, userModel: UserModel, imageModel: ImageModel, pipelineModel: PipelineModel, cache: CacheApi) extends Controller with I18nSupport {

  def loginUser = Action { implicit request =>
    val jsonData = request.body.asFormUrlEncoded
    val typeVal = jsonData.get("type")(0)
    val idVal = jsonData.get("id")(0)
    val pwdVal = jsonData.get("pwd")(0)    
    val loginData = User(0, idVal, pwdVal)    
    if(typeVal == "login") {
      val checkVal = userModel.retrieve(loginData)
      if(checkVal == null) {
        Ok("check id or password")
      } else {       
        Ok("success").withSession("uId"-> idVal, "pwd"-> pwdVal)
      }      
    } else {
      userModel.insert(loginData)
      Ok("sign in success")
    }
  }
  
  def emailDupCheck = Action { implicit request =>
    val emailData = request.body.asFormUrlEncoded.get.get("email").get(0)
    val foundemailNum = userModel.emaildupcheck(emailData)    
    //Logger.debug(foundemailNum.toString())    
    if(foundemailNum != 0) {
      Ok("1")
    } else {
      Ok("0")
    } 
  }
  def logoutUser = Action { implicit request => Redirect(routes.PageController.loginPage()).withNewSession }
  
  def jobDupCheck= Action{ implicit request =>
    val jobData = request.body.asFormUrlEncoded.get.get("jobName").get(0)
    val foundjob= imageModel.retrieve(jobData,1)
    if(foundjob.length!=0) {
      Ok("1")
    } else {
      Ok("0")
    } 
  }

  def launch = Action {
    implicit request =>
      Logger.debug("asdasdasd");
      request.session.get("uId") match {
        case Some(value) => {
          val user = value
//          val conf = Json.parse(request.body.asFormUrlEncoded.get.get("conf").get(0));
          val pipeline = Json.parse(request.body.asFormUrlEncoded.get.get("pipeline").get(0));
          
         
          val x = pipeline(1)          
          Logger.debug("pipeline:"+pipeline.toString())
//          val job_name = (conf \ "job_name").as[String]
//          val job_property =(conf \ "job_property").as[String]
//          val parent_name = (conf \ "parent_name").as[String]
//          val cpu = (conf \ "cpu").as[String]
//          val mem = (conf \ "mem").as[String]
//          val exes = (pipeline \\ "exe").map(_.as[String])
          
//          val user_path = "/nfsdir/" + user + "/"
//          val file_path = user_path + job_name
//          val file_name= (x \ "fileName").as[String]
//          val file_name = (pipeline.tail.get \ "fileName").as[JsString]
////
//          Process("mkdir -p " + file_path).run
//          Process("chmod 777 " + file_path).run
//          Process("touch " + file_path + "/docker.json").run
//          Process("touch " + file_path + "/Dockerfile").run
//          Process("touch " + file_path + "/innerSh.sh").run
//          writingDockerfile(file_path)
//          writingInnerSh(user_path, file_path, file_name, exes)
//          writingDockerJson(job_name,file_path,cpu,mem)
//          val currentDate=(new SimpleDateFormat("yyyy-MM-dd-HH-mm")).format(new Date).toString()
//          val imageData=Image(0,"yuhadam/"+job_name,job_name,job_property,parent_name,"fileUploading",currentDate,user)
//          imageModel.insert(imageData);
//
//          Logger.debug(job_name + "/" + cpu + "/" + mem + "/" + exes + "/" + file_path+"/"+file_name);
//          Logger.debug(pipeline.last.toString());
          Logger.debug(pipeline.toString());
        }
      }
      Ok("asdf");
    //      val bwa=BwaForm.bindFromRequest()
    //       bwa.fold(
    //           hasErrors=> Ok("input error")
    //           , success= { newBwa=>           
    //                          val now= (new SimpleDateFormat("yyyy-MM-dd-HH-mm")).format(new Date).toString()
    //                          val exe_name=bwa.data("exe_name")
    //                          val job_name=exe_name+"-"+now
    //                          val job_path="/nfsdir/"+job_name
    //                          val fileName= "MT.fa"
    //                          Process("mkdir "+job_path).run
    //                          Process("chmod 777 "+job_path).run    
    //                          Process("touch "+job_path+"/innerSh.sh").run
    //                          Process("touch "+job_path+"/Dockerfile").run
    //                          
    //                          val cmd=Bwa.getCmd(newBwa)
    //                          
    //                          writingInnerSh(job_path,exe_name,fileName,job_name,cmd+fileName )
    //                          writingDockerfile(job_path)
    //                          
    //                    
    //                          val launch=Seq("launch.sh",job_path,job_name)
    //                          Process(launch).run
    //                          
    //                          Ok(cmd)
    //             }
    //           )
  }
    def getImageTable = Action { implicit request =>
    val imageData = imageModel.retrieve(request.session.get("uId").getOrElse("none"),2)
    val jsonArray =  imageData.map { x => 
             Json.obj("pIndex" -> x.pIndex, "imgName" -> x.imgName, "jobName" -> x.jobName, "jobType" -> x.jobType, "parentInfo" -> x.parentInfo,
                 "status" -> x.status, "date" -> x.date, "uId" -> x.uId)       
       }   
    
     val json = JsArray(jsonArray)
     Ok(json.toString())
  }

  type FilePartHandler[A] = FileInfo => Accumulator[ByteString, FilePart[A]]
  def handleFilePartAsFile(size:Long,conf:String): FilePartHandler[File] = {    
    case FileInfo(partName, filename, contentType) =>
      val config= Json.parse(conf)
      Logger.debug(conf.toString());
      val jobName = (config \ "jobName").as[String]
      val jobType =(config \ "jobType").as[String]
      val parentInfo = (config \ "parentInfo").as[String]
      val cpu = (config \ "cpu").as[String]
      val mem = (config \ "mem").as[String]
      val uId = (config \"uId").as[String]
      val path= "/nfsdir/"+(config \ "uId").as[String]+"/"+jobName
      val currentDate=(new SimpleDateFormat("yyyy-MM-dd-HH-mm")).format(new Date).toString()
      val imageData=Image(0,"yuhadam/"+jobName,jobName,jobType,parentInfo,"FileUploading",currentDate,uId)
      imageModel.insert(imageData);
      Process("mkdir -p " + path).run
      Process("chmod 777 " + path).run
      
      val filepath = Paths.get(path+"/"+filename)
      val filesink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(filepath, Set(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE))
      val accumulator = Accumulator(filesink)
      accumulator.map {
        case IOResult(count, status) =>{
          
          if(size==count){
            FilePart(partName, filename, contentType, filepath.toFile())
          }
           else{
             imageModel.update(jobName,"FileUploadeFail")
             WebSocketActor.sendMessage(Json.stringify(Json.obj("jobName"->jobName,"status"->"FileUploadFile")), uId)
             Logger.debug("nononoonon")
             Process("rm -rf " +path).run
             FilePart(partName, "no", contentType, filepath.toFile())
           }
        }
      }(play.api.libs.concurrent.Execution.defaultContext)
  }

  def uploadCustom(size:Long, conf:String) = Action{parse.multipartFormData(handleFilePartAsFile(size,conf), 1000000000000L)}{ request =>
      val dataParts= request.body.dataParts.get("json").get(0);
      
      
      Logger.debug(dataParts.toString())
//      val pipeline=List();
    
      val config= Json.parse(conf)
      val jobName = (config \ "jobName").as[String]
      val uId = (config \"uId").as[String]
      val path= "/nfsdir/"+(config \ "uId").as[String]+"/"+jobName
      val cpu= (config \ "cpu").as[String]
      val mem= (config \ "mem").as[String]
      
      
    val fileOption = request.body.file("file").map {
    case FilePart(key, filename, contentType, file) =>
      if(filename!="no"){
        if(imageModel.retrieve("Running",3).length>=4){
          imageModel.update(jobName,"Pending")
          WebSocketActor.sendMessage(Json.stringify(Json.obj("jobName"->jobName,"status"->"Pending")), uId)
        }
        else{
          imageModel.update(jobName,"Running")
          WebSocketActor.sendMessage(Json.stringify(Json.obj("jobName"->jobName,"status"->"Running")), uId)
          }
        writingDockerfile(path)
        writingDockerJson(jobName,path,cpu,mem)
        Logger.debug("vvvvv")
     }
    else{
        Logger.debug("sdfafdasf");
     }
    }

//    Logger.debug(fileOption.toString())

//    val job_name= request.body.asFormUrlEncoded.get("jobname").get(0).toString()
//    
////    if(success)
////      if(Integer.parseInt(imageModel.retrieve("running", 3).toString())>=4){
////        imageModel.update(job_name,"pending")
////      }
////      else{
////        imageModel.update(job_name,"running")
////        val uId=request.session.get("uId")
////        val job_path="/nfsdir/"+uId+"/"+job_name+"/"
////        val launch=Seq("launch.sh",job_name,job_path)
////        Process(launch).run
////      }
////    else{}
//    
    Ok("good")
  }
  
  //////////////////////
  def sftpresult(data: String) = Action {
    implicit request =>
      val user = request.session.get("uId").toString();
      val result = Seq("mv", "-f", "/nfsdir/" + data, "/home/" + user)
      Process(result).run     
      Ok("ok")

  }

  private def writingDockerfile(filePath: String) = {
    val bw = new BufferedWriter(new FileWriter(filePath + "/Dockerfile"))
    bw.write("FROM yuhadam/baseimage")
    bw.newLine()
    bw.write("RUN apt-get update")
    bw.newLine()
    bw.write("RUN apt-get install -y curl")
    bw.newLine()
    bw.write("RUN apt-get install -y nfs-common")
    bw.newLine()
    bw.write("WORKDIR " + filePath + "/")
    bw.newLine()
    bw.write("CMD ./innerSh.sh")
    bw.close()
  }
  private def writingInnerSh(userPath: String, filePath: String, fileName: String, exes: Seq[String]) = {
    val bw = new BufferedWriter(new FileWriter(filePath + "/innerSh.sh"))
    bw.write("#! /bin/sh")
    bw.newLine()
    bw.write("set -e")
    bw.newLine()
    for (exe <- exes) {
      bw.write("ln /exe/" + exe + " " + filePath)
      bw.newLine()
    }
    bw.write("ln " + userPath + " " + fileName + " " + filePath)
    bw.newLine()
    bw.close();
    //     bw.write(cmd)
    //     bw.newLine()
    //     bw.write("rm "+exe_name)
    //     bw.newLine()
    //     bw.write("rm "+fileName)
    //     bw.newLine()
    //     bw.write("rm Dockerfile")
    //     bw.newLine()
    //     bw.write("rm docker.json")
    //     bw.newLine()
    //     bw.write("rm innerSh.sh")
    //     bw.newLine()
    //     bw.write("curl 211.249.63.201:9000/result/"+job_name)
    //     bw.newLine()
    //     bw.close()
    //     Process("sudo chmod 777 "+filePath+"/innerSh.sh").run

  }
  private def writingDockerJson(jobName:String,filePath:String,cpu:String,mem:String){
    val bw = new BufferedWriter(new FileWriter(filePath + "/docker.json"))
    bw.write("{")
    bw.newLine()
    bw.write("\"schedule\": \"R1/2014-09-25T17:22:00Z/PT2M\",")
    bw.newLine()
    bw.write("\"name\":\""+jobName+"\",")
    bw.newLine()
    bw.write("\"container\": {")
    bw.newLine()
    bw.write("\"type\": \"DOCKER\",")
    bw.newLine()
    bw.write("\"image\":\"yuhadam/"+jobName+"\"," )
    bw.newLine()
    bw.write("\"network\": \"BRIDGE\",")
    bw.newLine()
    bw.write("\"volumes\": [")
    bw.newLine()
    bw.write("{")
    bw.newLine()
    bw.write("\"containerPath\": \"/nfsdir\",")
    bw.newLine()
    bw.write("\"hostPath\": \"/nfsdir\",")
    bw.newLine()
    bw.write("\"mode\":\"RW\"")
    bw.newLine()
    bw.write("}")
    bw.newLine()
    bw.write("]")
    bw.newLine()
    bw.write("},")
    bw.newLine()
    bw.write("\"cpus\":\""+cpu+"\",")
    bw.newLine()
    bw.write("\"mem\":\""+mem+"\",")
    bw.newLine()
    bw.write("\"uris\": [],")
    bw.newLine()
    bw.write("\"command\":\""+filePath+"/innerSh.sh\"")
    bw.newLine()
    bw.write("}")
    bw.newLine()
    bw.close()   
  }
}