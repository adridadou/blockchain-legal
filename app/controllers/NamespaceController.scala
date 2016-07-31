package controllers

import javax.inject._

import play.api.Configuration
import play.api.mvc._
import services.EthereumService

@Singleton
class NamespaceController @Inject()(ethereum:EthereumService, conf:Configuration, webJarAssets:WebJarAssets) extends Controller {

  val admin = conf.getString("adridadou.ethereum.admin").getOrElse("")
  val user = conf.getString("adridadou.ethereum.user").getOrElse("")

  val adminKey = ethereum.key(admin).decode("")
  val userKey = ethereum.key(user).decode("")

  val contract = ethereum.contract(adminKey)

  lazy val namespaces = for(i <- 0 until contract.getNbNamespaces) yield contract.getNamespace(i)

  def index = Action { implicit request =>
    namespaces.foreach(println(_))
    Ok(views.html.namespace("Namespace explorer", webJarAssets))
  }


}
