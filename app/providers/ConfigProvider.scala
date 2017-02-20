package providers

import javax.inject.Inject

import com.google.inject.Provider
import org.adridadou.ethereum.EthereumFacade
import org.adridadou.ethereum.keystore.AccountProvider
import org.adridadou.ethereum.values.{ContractAbi, EthAccount, SoliditySource}

/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */
class ConfigProvider @Inject() (ethereum:EthereumFacade) extends Provider[BlockchainLegalConfig] {
  override def get(): BlockchainLegalConfig = BlockchainLegalConfig(legalContractManagerConfig)

  private val solidityCode = SoliditySource.from(getClass.getResourceAsStream("/public/solidity/legalContractManager.sol"))

  private val contractName = "LegalContractManager"

  private val abi = ethereum.compile(solidityCode, contractName).get().getAbi

  private def legalContractManagerConfig:LegalContractManagerConfig = LegalContractManagerConfig(abi, contractName)
}

case class BlockchainLegalConfig(legalContractManagerConfig:LegalContractManagerConfig) {
  def getAccount(id: String): EthAccount = AccountProvider.from(id)
}

case class LegalContractManagerConfig(abi:ContractAbi, name:String)
