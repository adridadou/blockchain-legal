package providers

import javax.inject.Singleton

import com.google.inject.Provider
import org.adridadou.ethereum.EthereumFacade
import org.adridadou.ethereum.provider.{EthereumFacadeProvider, EthereumJConfigs}
/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */

@Singleton
class EthereumProvider extends Provider[EthereumFacade] {

  override def get(): EthereumFacade = {
    val provider = EthereumFacadeProvider.forNetwork(EthereumJConfigs.ropsten())
    provider.extendConfig().fastSync(true)
    provider.create()
  }
}
