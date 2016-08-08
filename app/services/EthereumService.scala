package services

import javax.inject.{Inject, Singleton}

import org.adridadou.ethereum.keystore.SecureKey
import org.adridadou.ethereum.{EthAddress, EthereumFacade}
import org.ethereum.crypto.ECKey
import providers.BlockchainLegalConfig

/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */
@Singleton
class EthereumService @Inject() (ethereum:EthereumFacade, config:BlockchainLegalConfig) {
  val contractConfig = config.legalContractManagerConfig
  def getCurrentBlockNumber:Long = ethereum.getCurrentBlockNumber
  def isSyncDone:Boolean = ethereum.isSyncDone
  def key(id:String):SecureKey = ethereum.getKey(id)
  def contract(key:ECKey) : LegalContractManager = {
    ethereum.createContractProxy(contractConfig.code,contractConfig.name,contractConfig.address,key,classOf[LegalContractManager])
  }
}


trait LegalContractManager {
  /*
		Get the source of the package. Right now this is an IPFS hash but it should be possible to have any way of retrieving the source (HTTP, FTP etc ...)

		params:
		- Namespace: The namespace of the package you are looking for
		- name: the name of the legal documents
		- version: the version of the legal documents
	*/
  def getSource(namespace:String, name:String, version:String, protocol:String):String

  /*
    Register a new package.

    params:
    - Namespace: The namespace of the package you are looking for
    - name: the name of the legal documents
    - version: the version of the legal documents
  */
  def register(namespace:String, name:String, version:String, protocol:String, source:String, checksum:String):Unit

  def getChecksum(namespace:String, name:String, version:String):String

  /*
    Create a new namespace and makes the tx.origin the owner of this namespace
  */
  def createNamespace(namespace:String, owner:Array[Byte]):Unit

  /*
    Passes the ownership of a namespace to someone else
  */
  def changeOwner(namespace:String, newOwner:EthAddress):Unit

  def canWrite(namespace:String, user:EthAddress):Boolean


  def getNbNamespaces:Int

  def getNamespace(id:Int):String

  def getNbProjects(namespace:String):Int

  def getProject(namespace:String,id:Int):String

  def getNbVersions(namespace:String,project:String):Int

  def getVersion(namespace:String, project:String, id:Int):String

  def getOwner(namespace:String) :EthAddress
}
