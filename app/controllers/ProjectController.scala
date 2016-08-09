package controllers

import java.io.{File, FileInputStream}
import java.security.MessageDigest
import javax.inject._

import org.spongycastle.util.encoders.Hex
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{EthereumService, IpfsService}

@Singleton
class ProjectController @Inject()(ethereum:EthereumService, conf:Configuration, webJarAssets:WebJarAssets, ipfsService:IpfsService) extends Controller {

  val admin = conf.getString("adridadou.ethereum.admin").getOrElse("")
  val user = conf.getString("adridadou.ethereum.user").getOrElse("")

  val adminKey = ethereum.key(admin).decode("")
  val userKey = ethereum.key(user).decode("")

  def contract = ethereum.contract(adminKey)
  def userContract = ethereum.contract(userKey)

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


  def create(namespace:String, projectName:String, version:String) = Action {implicit request =>
    val file = zip(new File("uploaded").listFiles())
    val chksm = checksum(file)
    val ipfsHash = ipfsService.add(file).getOrElse("")
    println("ipfs hash:" + ipfsHash)

    userContract.register(namespace,projectName,version,"ipfs", ipfsHash, chksm)
    Redirect("/project/" + namespace)
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

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { file =>
      import java.io.File
      val filename = file.filename
      new File("uploaded").mkdir()
      file.ref.moveTo(new File(s"uploaded/$filename"))
      Ok("File uploaded")
    }.getOrElse {
      NotFound("file not found!")
    }
  }

  def zip(files: Iterable[File]):File = {
    import java.io.{ BufferedInputStream, FileInputStream, FileOutputStream }
    import java.util.zip.{ ZipEntry, ZipOutputStream }
    val file = File.createTempFile("launcheth",".zip")
    val zip = new ZipOutputStream(new FileOutputStream(file))

    files.foreach { f =>
      zip.putNextEntry(new ZipEntry(f.getName))
      val in = new BufferedInputStream(new FileInputStream(f))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()
    }
    zip.close()
    file
  }

}
