package com.zpi.domain.audit;

import com.zpi.domain.common.EntityRepository;
import com.zpi.domain.organization.Organization;

import java.util.Date;
import java.util.List;

public interface AuditRepository extends EntityRepository<Date, AuditLog> {
    List<AuditLog> findByUsername(String username);
}
