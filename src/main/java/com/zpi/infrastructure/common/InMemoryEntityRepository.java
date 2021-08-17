package com.zpi.infrastructure.common;

import com.zpi.domain.common.EntityRepository;
import lombok.Getter;

import java.util.HashMap;
import java.util.Optional;

@Getter
public abstract class InMemoryEntityRepository<Type, TypeTuple extends EntityTuple<Type>> implements EntityRepository<Type> {
    private final HashMap<String, TypeTuple> items = new HashMap<>();

    @Override
    public abstract void save(String key, Type item);

    @Override
    public Optional<Type> getByKey(String key) {
        var tuple = Optional.ofNullable(items.get(key));
        return tuple.map(TypeTuple::toDomain);
    }

    @Override
    public void clear() {
        items.clear();
    }
}
