package main

import container.CsvEventLoader
import container.LambdaContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import scheduler.IClock
import scheduler.RealtimeClock
import scheduler.SimulationClock
import spec.IEventLoader
import thread.Dispatcher
import thread.IDispatcher

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

    @Bean(name = ["csvEventLoader"])
    open fun csvEventLoader(): IEventLoader<*> = CsvEventLoader()

    @Bean(name = ["httpPort"])
    open fun httpPort(): Int = 8888

    @Bean(name = ["wsPort"])
    open fun wsPort(): Int = 8889

}
