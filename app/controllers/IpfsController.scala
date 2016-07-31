package controllers

import javax.inject._

import org.ipfs.api.Multihash
import play.api.mvc._
import services.IpfsService

@Singleton
class IpfsController @Inject()(ipfsService:IpfsService) extends Controller {

  def getFile(hash:String) = Action {
    val filePointer = Multihash.fromBase58(hash)
    val fileContents = ipfsService.cat(filePointer)
    Ok(fileContents)
  }

}
