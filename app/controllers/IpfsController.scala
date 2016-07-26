package controllers

import javax.inject._

import org.ipfs.api.{IPFS, Multihash}
import play.api.mvc._

@Singleton
class IpfsController @Inject()(ipfs:IPFS) extends Controller {


  def getFile(hash:String) = Action {
    val filePointer = Multihash.fromBase58(hash)
    val fileContents = ipfs.cat(filePointer)
    
    Ok("")
  }

}
