package com.zpi.infrastructure.audit;

import com.zpi.domain.audit.AuditData;
import com.zpi.domain.audit.AuditRepository;
import com.zpi.domain.organization.Organization;
import com.zpi.infrastructure.common.EntityTuple;
import com.zpi.infrastructure.common.InMemoryRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryAuditRepository extends InMemoryRepository<Date, AuditData> implements AuditRepository {
    @Override
    public EntityTuple<AuditData> fromDomain(AuditData data) {
        return new AuditTuple(data);
    }

    @Override
    public List<AuditData> findByOrganization(Organization organization) {
        var organizationName = organization.getName();
        var entriesForOrganization = super.repository.entrySet().stream().filter(
                (entry) -> entry.getValue().toDomain().getOrganizationName().equals(organizationName)
        ).collect(Collectors.toList());

        return entriesForOrganization.stream().map(
                entry -> entry.getValue().toDomain()
        ).collect(Collectors.toList());
    }
}
