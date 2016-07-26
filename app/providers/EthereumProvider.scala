package providers

import java.io.{File, FileInputStream}

import com.google.common.base.Charsets
import com.google.inject.Provider
import org.adridadou.ethereum.EthereumFacade
import org.adridadou.ethereum.provider.TestnetEthereumFacadeProvider
import org.apache.commons.io.IOUtils

/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */
class EthereumProvider extends Provider[EthereumFacade] {

  override def get(): EthereumFacade = {
    val provider = new TestnetEthereumFacadeProvider()
    val key = provider.getKey("cow","")
    provider.create(key)
  }
}
