package base.container

import org.joda.time.DateTime
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class EventsLoaderContainerTest {

    @Test
    fun init() {
        val d = DateTime.parse("2017-12-12T09:31:45.123")
        println(d.millis)
    }
}