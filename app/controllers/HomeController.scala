package controllers

import javax.inject._

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.adridadou.ethereum.EthereumFacade
import play.api.libs.json.{JsString, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import providers.BlockchainLegalConfig
import services.IpfsService

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(ethereum:EthereumFacade, config:BlockchainLegalConfig, ipfs:IpfsService, webJarAssets:WebJarAssets)(implicit system: ActorSystem, materializer: Materializer) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
    Ok(views.html.index("Blockchain Legal - Home", webJarAssets))
  }

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => BlockchainStatusActor.props(out,ethereum, ipfs))
  }
}

import akka.actor._

object BlockchainStatusActor {
  def props(out: ActorRef, ethereum:EthereumFacade, ipfs:IpfsService) = Props(new BlockchainStatusActor(out, ethereum, ipfs))
}

class BlockchainStatusActor(out: ActorRef, ethereum:EthereumFacade, ipfs:IpfsService) extends Actor {
  def receive = {
    case "ethereumState" => out ! Json.obj(
      "blockNumber" -> ethereum.getCurrentBlockNumber,
      "syncDone" -> ethereum.isSyncDone.toString,
      "ipfsStatus" -> (ipfs.ipfs match {
        case None => JsString("connection error")
        case Some(_) => JsString("connected")
      })
    ).toString()
    case msg: String =>
      out ! ("I received your message: " + msg)
  }
}
