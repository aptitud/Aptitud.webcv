package se.webcv.repository.inmem;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by marcus on 20/01/15.
 */
public abstract class AbstractInMemoryRepository<Id, V> {
    protected final ConcurrentMap<Id, V> data = new ConcurrentHashMap<>();

    public V findById(Id id) {
        return data.get(id);
    }

    public Collection<V> findAll() {
        return data.values();
    }

    public Collection<Id> allKeys() {
        return data.keySet();
    }

    public V save(Id id, V v) {
        data.put(id, v);
        return v;
    }
}
