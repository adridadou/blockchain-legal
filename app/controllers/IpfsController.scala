package controllers

import java.io.ByteArrayInputStream
import java.security.MessageDigest
import javax.inject._

import org.adridadou.ethereum.values.EthAddress
import org.ipfs.api.Multihash
import org.spongycastle.util.encoders.Hex
import play.api.Configuration
import play.api.mvc._
import services.{EthereumService, IpfsService}

@Singleton
class IpfsController @Inject()(ipfsService:IpfsService, ethereum:EthereumService, conf:Configuration) extends Controller {

  val admin = conf.getString("adridadou.ethereum.admin").getOrElse("")
  val user = conf.getString("adridadou.ethereum.user").getOrElse("")

  val adminKey = ethereum.key(admin).decode("")
  val userKey = ethereum.key(user).decode("")

  def contract = ethereum.contract(adminKey, EthAddress.of(""))
  def userContract = ethereum.contract(userKey, EthAddress.of(""))

  def getFile(namespace:String, project:String, version:String) = Action {
    val hash = contract.getSource(namespace,project,version,"ipfs")
    val expectedChecksum = contract.getChecksum(namespace,project,version)
    val display = project + "_" + version + ".zip"

    val filePointer = Multihash.fromBase58(hash)
    val fileContents = ipfsService.cat(filePointer)

    if(expectedChecksum.equals(checksum(fileContents))){
      ipfsService.add(fileContents)


      Ok(fileContents)
        .as("application/x-download")
        .withHeaders(
          CACHE_CONTROL->"max-age=3600",
          CONTENT_DISPOSITION-> ("attachment; filename="+ display + ".zip")
        )
    } else {
      InternalServerError("checksum mismatch!")
    }


  }

  private def checksum(fileContent:Array[Byte]):String = {
    val md = MessageDigest.getInstance("SHA-256")
    val in = new ByteArrayInputStream(fileContent)
    val block = new Array[Byte](4096)
    var length = in.read(block)
    while (length > 0) {
      md.update(block, 0, length)
      length = in.read(block)
    }

    Hex.toHexString(md.digest())
  }
}
