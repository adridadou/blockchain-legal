package providers

import com.google.inject.Provider
import services.IpfsService

/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */
class IpfsProvider extends Provider[IpfsService]{
  override def get(): IpfsService = {
    new IpfsService("/ip4/ipfs.infura.io/tcp/5001")
  }
}
