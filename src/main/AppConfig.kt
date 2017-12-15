package main

import base.container.EventsLoaderContainer
import base.container.LambdaContainer
import base.thread.Dispatcher
import base.thread.IDispatcher
import base.thread.scheduler.IClock
import base.thread.scheduler.RealtimeClock
import base.thread.scheduler.SimulationClock
import org.joda.time.DateTime
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AppConfig {

    @Bean(name = ["dispatcher"])
    open fun dispatcher(): IDispatcher = Dispatcher()

    @Bean(name = ["realtimeClock"])
    open fun realtimeClock() = RealtimeClock(dispatcher())

    @Bean(name = ["replayClock"])
    open fun replayClock(): SimulationClock {
        val c = SimulationClock(dispatcher())
        val dt = DateTime.now()
        c.currentTimeInMs = DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), 6, 0, 0, dt.getZone()).millis
        return c
    }

    @Bean(name = ["referenceClock"])
    open fun referenceClock(): IClock = replayClock()


    @Bean(name = ["lambda"])
    open fun lambda(): LambdaContainer {
        val httpPort = 8888

        val wsPort = 8889

        return LambdaContainer(dispatcher = dispatcher(), clock = referenceClock(), httpPort = httpPort, wsPort = wsPort)
    }

    @Bean(name = ["eventsLoader"])
    open fun eventsLoader(): EventsLoaderContainer {
        val el = EventsLoaderContainer(replayClock())
        el.marketDataFilePath = "test/quotes.txt"
        el.init()

        return el
    }

}
