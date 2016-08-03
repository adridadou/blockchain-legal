package controllers

import javax.inject._

import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.EthereumService

@Singleton
class NamespaceController @Inject()(ethereum:EthereumService, conf:Configuration, webJarAssets:WebJarAssets) extends Controller {

  val admin = conf.getString("adridadou.ethereum.admin").getOrElse("")
  val user = conf.getString("adridadou.ethereum.user").getOrElse("")

  val adminKey = ethereum.key(admin).decode("")
  val userKey = ethereum.key(user).decode("")

  val contract = ethereum.contract(adminKey)

  lazy val namespaces:Seq[JsValue] = for(i <- 0 until contract.getNbNamespaces) yield {
    val namespace = contract.getNamespace(i)
    Json.toJson(Map(
      "name" -> namespace,
      "owner" -> contract.getOwner(namespace).toString,
      "nbProjects" -> contract.getNbProjects(namespace).toString
    ))
  }

  def index = Action { implicit request =>
    Ok(views.html.namespace("Namespace explorer", webJarAssets))
  }

  def getNamespaces = Action {request =>
    Ok(Json.toJson(namespaces))
  }


  def create(name:String) = Action {implicit request =>
    contract.createNamespace(name, userKey.getAddress)
    Redirect("/namespace")
  }

}
