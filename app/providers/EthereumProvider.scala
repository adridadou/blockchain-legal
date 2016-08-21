package providers

import com.google.inject.Provider
import org.adridadou.ethereum.EthereumFacade
import org.adridadou.ethereum.provider.TestnetEthereumFacadeProvider
/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */
class EthereumProvider extends Provider[EthereumFacade] {
  override def get(): EthereumFacade = {
    EthereumProvider.provider.create()
  }
}


object EthereumProvider {
  val provider = new TestnetEthereumFacadeProvider()
}
