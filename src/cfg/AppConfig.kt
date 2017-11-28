package cfg

import container.EventLoader
import container.LambdaContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import scheduler.IClock
import scheduler.RealtimeClock
import scheduler.SimulationClock
import thread.Dispatcher
import thread.IDispatcher

@Configuration
open class AppConfig {

    @Bean(name = arrayOf("dispatcher"))
    open fun dispatcher(): IDispatcher = Dispatcher()

    @Bean(name = arrayOf("clock"))
    open fun clock(): IClock = RealtimeClock(dispatcher())

    @Bean(name = arrayOf("replayClock"))
    open fun replayClock(): IClock = SimulationClock(dispatcher())

    @Bean(name = arrayOf("lambda"))
    open fun lambda(): LambdaContainer = LambdaContainer(dispatcher = dispatcher(), clock = clock(), httpPort = httpPort(), wsPort = wsPort())

    @Bean(name = arrayOf("eventLoader"))
    open fun eventLoader(): EventLoader = EventLoader()

    @Bean(name = arrayOf("httpPort"))
    open fun httpPort(): Int = 8888

    @Bean(name = arrayOf("WS_PORT"))
    open fun wsPort(): Int = 8889

}
