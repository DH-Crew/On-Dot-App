package com.ondot.calendar.data.cache

class CalendarDateScheduleCache<K, V>(
    private val maxSize: Int = 31,
) {
    init {
        require(maxSize > 0) { "maxSize must be greater than 0" }
    }

    private val map = LinkedHashMap<K, V>()

    operator fun get(key: K): V? {
        val value = map.remove(key) ?: return null
        map[key] = value
        return value
    }

    fun put(
        key: K,
        value: V,
    ) {
        if (map.containsKey(key)) {
            map.remove(key)
        }
        map[key] = value

        if (map.size > maxSize) {
            val eldestKey = map.keys.firstOrNull()
            if (eldestKey != null) {
                map.remove(eldestKey)
            }
        }
    }

    fun remove(key: K): V? = map.remove(key)

    fun clear() = map.clear()

    fun containsKey(key: K): Boolean = map.containsKey(key)

    operator fun set(
        key: K,
        value: V,
    ) = put(key, value)

    fun toMap(): Map<K, V> = map.toMap()
}
