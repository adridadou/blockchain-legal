package controllers

import javax.inject._

import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.EthereumService

@Singleton
class ProjectController @Inject()(ethereum:EthereumService, conf:Configuration, webJarAssets:WebJarAssets) extends Controller {

  val admin = conf.getString("adridadou.ethereum.admin").getOrElse("")
  val user = conf.getString("adridadou.ethereum.user").getOrElse("")

  val adminKey = ethereum.key(admin).decode("")
  val userKey = ethereum.key(user).decode("")

  val contract = ethereum.contract(adminKey)

  def projects(namespace:String):Seq[JsValue] = for(i <- 0 until contract.getNbProjects(namespace)) yield {
    val project = contract.getProject(namespace,i)
    Json.toJson(Map("name" -> project, "nbVersions" -> contract.getNbVersions(namespace,project).toString))
  }

  def index(namespace:String) = Action { implicit request =>
    Ok(views.html.project("Project explorer", namespace, webJarAssets))
  }

  def getProjects(namespace:String) = Action {request =>
    Ok(Json.toJson(projects(namespace)))
  }


  def create(namespace:String, projectName:String, version:String, ipfs:String) = Action {implicit request =>
    contract.register(namespace,projectName,version,ipfs)
    Redirect("/project/" + namespace)
  }

}
