package annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class AlgoInfo(val mode: Mode = Mode.UAT, val activities: Array<Activity>)

enum class Mode {
    SIM, UAT, PRD
}

enum class Activity {
    Signal, // Inter algo communication
    Report, // Write to storage
    Trading // Send orders.
}

