package controllers

import java.io.{File, FileInputStream}
import java.security.MessageDigest
import javax.inject._

import org.adridadou.ethereum.values.EthAddress
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

  def contract = ethereum.contract(adminKey, EthAddress.of(""))
  def userContract = ethereum.contract(userKey, EthAddress.of(""))

  private val uploadedFile = new File("uploaded")

  def projects(namespace:String):Seq[JsValue] = for(i <- 0 until contract.getNbProjects(namespace)) yield {
    val project = contract.getProject(namespace,i)
    Json.toJson(Map("name" -> project, "nbVersions" -> contract.getNbVersions(namespace,project).toString))
  }

  def packages(namespace:String, project:String) = Action{ request =>
    val nbVersions = contract.getNbVersions(namespace,project)
    val result:Seq[Map[String, String]] = (0 until contract.getNbVersions(namespace, project)).map(i => {
      val version = contract.getVersion(namespace, project, i)

      Map(
        "name" -> version,
        "ipfs" -> contract.getSource(namespace, project, version, "ipfs"),
        "checksum" -> contract.getChecksum(namespace,project,version)
      )
    })

    Ok(Json.toJson(result))
  }

  def index(namespace:String) = Action { implicit request =>
    Ok(views.html.project("Project explorer", namespace, webJarAssets))
  }

  def packagePage(namespace:String, project:String) = Action {implicit request =>
    Ok(views.html.file("Package explorer", namespace, project, webJarAssets))
  }

  def getProjects(namespace:String) = Action {request =>
    Ok(Json.toJson(projects(namespace)))
  }

  def getFiles = Action {request =>
    Ok(Json.toJson(uploadedFile.list().toSeq))
  }

  def removeFile(namespace:String, name:String) = Action { request =>
    uploadedFile.listFiles().filter(_.getName.equals(name)).foreach(_.delete())
    Redirect(s"/project/$namespace")
  }

  def create(namespace:String, projectName:String, version:String) = Action {implicit request =>
    val file = zip(uploadedFile.listFiles())
    val chksm = checksum(file)
    val ipfsHash = ipfsService.add(file).getOrElse("")

    userContract.register(namespace,projectName,version,"ipfs", ipfsHash, chksm)
    uploadedFile.listFiles().foreach(_.delete())
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
      uploadedFile.mkdir()
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
