package providers

import com.google.inject.Provider
import org.adridadou.ethereum.keystore.SecureKey
import org.adridadou.ethereum.values.SoliditySource

/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */
class ConfigProvider extends Provider[BlockchainLegalConfig] {
  override def get(): BlockchainLegalConfig = BlockchainLegalConfig(legalContractManagerConfig)

  private val solidityCode = getClass.getResourceAsStream("/public/solidity/legalContractManager.sol")

  private def legalContractManagerConfig:LegalContractManagerConfig = LegalContractManagerConfig(SoliditySource.from(solidityCode),"LegalContractManager")
}

case class BlockchainLegalConfig(legalContractManagerConfig:LegalContractManagerConfig) {
  def getKey(id: String): SecureKey = EthereumProvider.provider.getKey(id)
}

case class LegalContractManagerConfig(code:SoliditySource,name:String)
