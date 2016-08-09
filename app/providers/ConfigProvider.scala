package providers

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

  private val solidityCode = getClass.getResourceAsStream("/public/solidity/legalContractManager.sol")

  private def legalContractManagerConfig:LegalContractManagerConfig = LegalContractManagerConfig(IOUtils.toString(solidityCode,Charsets.UTF_8),"LegalContractManager",EthAddress.of("84975519ba514d121602258108bd443e8b3221d8"))
}

case class BlockchainLegalConfig(legalContractManagerConfig:LegalContractManagerConfig)

case class LegalContractManagerConfig(code:String,name:String, address:EthAddress)
