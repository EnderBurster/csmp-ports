package aureum.asta.disks.ports.elysium.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
public class OptionalMap<K, V> {
    private final Map<K, V> backingMap = new ConcurrentHashMap<>();

    // Returns Optional instead of raw V
    public Optional<V> get(Object key) {
        return Optional.ofNullable(backingMap.get(key));
    }

    // Standard Map methods

    public V put(K key, V value) {
        return backingMap.put(key, value);
    }

    public boolean containsKey(K key) {
        return backingMap.containsKey(key);
    }

    public V remove(K key) {
        return backingMap.remove(key);
    }

    public void clear() {
        backingMap.clear();
    }

    public Set<K> keySet() {
        return backingMap.keySet();
    }

    public Collection<V> values() {
        return backingMap.values();
    }

    public int size() {
        return backingMap.size();
    }

    public boolean isEmpty() {
        return backingMap.isEmpty();
    }
}