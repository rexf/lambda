package annotation

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
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

