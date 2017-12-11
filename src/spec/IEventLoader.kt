package spec

interface IEventLoader<out R> {
    data class MapEntry<out K, out V>(val key: K, val value: V)
    fun <T> loadAsList(producer: (R) -> T, comparator: Comparator<in T>? = null, skipIfFailed: Boolean = true): List<T>

    fun <K, V> loadAsMap(producer: (R) -> MapEntry<K, V>, skipIfFailed: Boolean = true): Map<K, V>
}