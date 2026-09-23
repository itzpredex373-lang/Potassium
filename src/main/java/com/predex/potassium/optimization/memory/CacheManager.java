package com.predex.potassium.optimization.memory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CacheManager<K,V> {
    private final Map<K,V> cache;
    private final int capacity;

    public CacheManager(int capacity) {
        this.capacity = Math.max(1, capacity);
        this.cache = new LinkedHashMap<K,V>(this.capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                return size() > CacheManager.this.capacity;
            }
        };
    }

    public synchronized V get(K key) { return cache.get(key); }
    public synchronized void put(K key, V value) { cache.put(key, value); }
    public synchronized void remove(K key) { cache.remove(key); }
    public synchronized void clear() { cache.clear(); }
    public synchronized int size() { return cache.size(); }
}