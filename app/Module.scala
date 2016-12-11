import com.google.inject.AbstractModule
import java.time.Clock

import org.adridadou.ethereum.EthereumFacade
import providers.{BlockchainLegalConfig, ConfigProvider, EthereumProvider, IpfsProvider}
import services.IpfsService

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    bind(classOf[EthereumFacade]).toProvider(classOf[EthereumProvider]).asEagerSingleton()
    bind(classOf[IpfsService]).toProvider(classOf[IpfsProvider]).asEagerSingleton()
    bind(classOf[BlockchainLegalConfig]).toProvider(classOf[ConfigProvider]).asEagerSingleton()
  }

}
