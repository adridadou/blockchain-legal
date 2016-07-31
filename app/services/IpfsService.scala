package services

import org.ipfs.api.{IPFS, Multihash}

import scala.util.{Failure, Success, Try}

/**
  * Created by davidroon on 31.07.16.
  * This code is released under Apache 2 license
  */
class IpfsService(val url:String) {
  private val ipfsConnection:Try[IPFS] = initIpfs
  private def initIpfs = {
    Try(new IPFS(url))
  }

  def error:Option[Throwable] = ipfsConnection match {
    case Success(_) => None
    case Failure(ex) => Some(ex)
  }

  def ipfs:Option[IPFS] = ipfsConnection.toOption
  def cat(filePointer: Multihash):Array[Byte] = {
    ipfs.map(_.cat(filePointer)).getOrElse(Array[Byte]())
  }
}
