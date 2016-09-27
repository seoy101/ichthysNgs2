package controllers

import play.api._
import play.api.mvc._
import akka.actor._
import akka.stream.Materializer
import play.api.libs.streams._
import javax.inject._


class PageController @Inject() (implicit system: ActorSystem, materializer: Materializer) extends Controller{
  
    def loginPage = Action { implicit request =>
      Ok(views.html.login())
    }
    
    def main = Action { implicit request =>
      request.session.get("uId") match{
        case Some(v) => Ok(views.html.main(v)) 
        case None   => Ok(views.html.login())
      }
    }

   
   def getHtml(data: String) = Action {
    implicit request =>
      val text = "views.html." + data
      Logger.debug(text)
      if (data == "test")
        Ok(views.html.test())
      else if (data == "test2")
        Ok(views.html.test2())
      else if (data == "test3")
        Ok(views.html.test3())
      else if (data == "pipeline")
        Ok(views.html.pipeline())
      else if(data== "file")
        Ok(views.html.fileupload())
      else if(data=="jobCreate")
        Ok(views.html.jobCreate())
      else if(data=="jobList")
        Ok(views.html.jobList())
        else
        Ok(views.html.view())
  }
   def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => WebSocketActor.props(out))
  }
}