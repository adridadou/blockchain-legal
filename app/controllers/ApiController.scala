package controllers

import java.io.{ByteArrayInputStream, File, FileInputStream}
import java.security.MessageDigest
import javax.inject._

import org.adridadou.ethereum.values.EthAddress
import org.ipfs.api.Multihash
import org.spongycastle.util.encoders.Hex
import play.api.Configuration
import play.api.mvc._
import services.{EthereumService, IpfsService}

@Singleton
class ApiController @Inject()(ethereum:EthereumService, ipfsService:IpfsService, conf:Configuration) extends Controller {

  val admin = conf.getString("adridadou.ethereum.admin").getOrElse("")
  val user = conf.getString("adridadou.ethereum.user").getOrElse("")
  val namespaceTemplate = conf.getString("adridadou.ethereum.namespace.template").getOrElse("")
  val namespaceDocument = conf.getString("adridadou.ethereum.namespace.document").getOrElse("")
  val namespaceSignature = conf.getString("adridadou.ethereum.namespace.signature").getOrElse("")

  val adminAccount = ethereum.key(admin).decode("")
  val userAccount = ethereum.key(user).decode("")

  private val uploadedFile = new File("uploaded")

  private def contractFactory(address:String) = ethereum.contract(adminAccount, EthAddress.of(address))

  def getTemplate(address:String, name:String, version:String) = Action {implicit request =>
    val contract = contractFactory(address)
    val hash = contract.getSource(namespaceTemplate,name,version,"ipfs")
    val expectedChecksum = contract.getChecksum(namespaceTemplate,name,version)
    val display = name + "_" + version + ".zip"

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

  def newTemplate(address:String, name:String, version:String) = Action(parse.multipartFormData) {implicit request =>
    request.body.file("file").map { uploadFile =>
      val contract = contractFactory(address)
      val filename = uploadFile.filename
      uploadedFile.mkdir()
      val file = new File(s"uploaded/$filename")
      uploadFile.ref.moveTo(file)

      val chksm = checksum(file)
      val ipfsHash = ipfsService.add(file).getOrElse("")

      contract.register(namespaceTemplate,name,version,"ipfs", ipfsHash, chksm)
      Ok("File uploaded")
    }.getOrElse {
      NotFound("file not found!")
    }
  }

  def signDocument(address:String, name:String, version:String, documentChecksum:String) = Action {implicit request =>
    request.body.asText.map { signature =>
      val contract = contractFactory(address)
      val templateChecksum = contract.getChecksum(namespaceTemplate, name, version)

      contract.register(namespaceSignature, templateChecksum, documentChecksum, "signature", signature, "")
      Ok("Signature created!")
    }.getOrElse {
      NotFound("Signature missing!")
    }
  }

  def newDocument(address:String, name:String, version:String) = Action {implicit request =>
    request.body.asText.map { params =>
      val contract = contractFactory(address)

      val templateChecksum = contract.getChecksum(namespaceTemplate,name,version)

      val chksm = checksum(params.getBytes)
      val ipfsHash = ipfsService.add(params.getBytes()).getOrElse("")

      contract.register(namespaceDocument,templateChecksum,chksm,"ipfs", ipfsHash, chksm)
      Ok("Document created!")
    }.getOrElse {
      NotFound("JSON missing!")
    }
  }

  def getDocuments(address:String, name:String, version:String) = Action {implicit request =>
    NotImplemented("not yet implemented")
  }

  def getDocumentParams(address:String, name:String, version:String, checksum:String) = Action {implicit request =>
    NotImplemented("not yet implemented")
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

  private def checksum(file:File):String = {
    val md = MessageDigest.getInstance("SHA-256")
    val in = new FileInputStream(file)
    val block = new Array[Byte](4096)
    var length = in.read(block)
    while (length > 0) {
      md.update(block, 0, length)
      length = in.read(block)
    }

    Hex.toHexString(md.digest())
  }

}
