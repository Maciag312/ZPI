package com.zpi.domain.common;

import java.util.Optional;

public interface EntityRepository<T> {
    void save(String key, T item);

    Optional<T> getByKey(String key);

    void clear();
}
