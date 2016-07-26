package providers

import com.google.inject.Provider
import org.ipfs.api.IPFS

/**
  * Created by davidroon on 24.07.16.
  * This code is released under Apache 2 license
  */
class IpfsProvider extends Provider[IPFS]{
  override def get(): IPFS = new IPFS("/ip4/127.0.0.1/tcp/5001");
}
