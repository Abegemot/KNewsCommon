package com.begemot.knewscommon.datacontainer


class ZBiHashMap<K, V> : HashMap<K, V>() {
    private var rMap: MutableMap<V, K> = HashMap()
    override fun put(key: K, value: V): V? {
        rMap[value] = key
        return super.put(key, value)
    }

    fun getKey(target: V): K? {
        return rMap[target]
    }
}

