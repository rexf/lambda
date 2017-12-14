package main

import container.EventsLoaderContainer
import container.LambdaContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import thread.Dispatcher
import thread.IDispatcher
import thread.scheduler.IClock
import thread.scheduler.RealtimeClock
import thread.scheduler.SimulationClock

@Configuration
open class AppConfig {

    @Bean(name = ["dispatcher"])
    open fun dispatcher(): IDispatcher = Dispatcher()

    @Bean(name = ["clock"])
    open fun clock(): IClock = RealtimeClock(dispatcher())

    @Bean(name = ["replayClock"])
    open fun replayClock(): IClock = SimulationClock(dispatcher())

    @Bean(name = ["lambda"])
    open fun lambda(): LambdaContainer = LambdaContainer(dispatcher = dispatcher(), clock = clock(), httpPort = httpPort(), wsPort = wsPort())

    @Bean(name = ["httpPort"])
    open fun httpPort(): Int = 8888

    @Bean(name = ["wsPort"])
    open fun wsPort(): Int = 8889

    @Bean(name = ["eventsLoader"])
    open fun eventsLoader() : EventsLoaderContainer {
      val el = EventsLoaderContainer(replayClock())
        el.marketDataFilePath = "test/quotes.txt"
        el.init()

        return el
    }

}
