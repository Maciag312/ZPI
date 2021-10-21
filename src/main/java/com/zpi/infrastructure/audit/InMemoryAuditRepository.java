package com.zpi.infrastructure.audit;

import com.zpi.domain.audit.AuditLog;
import com.zpi.domain.audit.AuditRepository;
import com.zpi.infrastructure.common.EntityTuple;
import com.zpi.infrastructure.common.InMemoryRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryAuditRepository extends InMemoryRepository<Date, AuditLog> implements AuditRepository {
    @Override
    public EntityTuple<AuditLog> fromDomain(AuditLog data) {
        return new AuditLogTuple(data);
    }

    @Override
    public List<AuditLog> findByUsername(String username) {
        var entriesUsername = super.repository.entrySet().stream().filter(
                (entry) -> entry.getValue().toDomain().getUsername().equals(username)
        ).collect(Collectors.toList());

        return entriesUsername.stream().map(entry -> entry.getValue().toDomain()).collect(Collectors.toList());
    }
}
