package providers

import java.io.{File, FileInputStream}

import com.google.common.base.Charsets
import com.google.inject.Provider
import org.adridadou.ethereum.EthAddress
import org.apache.commons.io.IOUtils

/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */
class ConfigProvider extends Provider[BlockchainLegalConfig] {
  override def get(): BlockchainLegalConfig = BlockchainLegalConfig(legalContractManagerConfig)

  private val solidityCode = new File("public/solidity/legalContractManager.sol")

  private def legalContractManagerConfig:LegalContractManagerConfig = LegalContractManagerConfig(IOUtils.toString(new FileInputStream(solidityCode),Charsets.UTF_8),"LegalContractManager",EthAddress.of("95b9d02c950da6f95630450927ed5a19cd881649"))
}

case class BlockchainLegalConfig(legalContractManagerConfig:LegalContractManagerConfig)

case class LegalContractManagerConfig(code:String,name:String, address:EthAddress)
