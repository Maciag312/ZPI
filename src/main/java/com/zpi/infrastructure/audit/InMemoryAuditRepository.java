package com.zpi.infrastructure.audit;

import com.zpi.domain.audit.AuditLog;
import com.zpi.domain.audit.AuditRepository;
import com.zpi.domain.organization.Organization;
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
    public List<AuditLog> findByOrganization(Organization organization) {
        var organizationName = organization.getName();
        var entriesForOrganization = super.repository.entrySet().stream().filter(
                (entry) -> entry.getValue().toDomain().getOrganizationName().equals(organizationName)
        ).collect(Collectors.toList());

        return entriesForOrganization.stream().map(
                entry -> entry.getValue().toDomain()
        ).collect(Collectors.toList());
    }
}
