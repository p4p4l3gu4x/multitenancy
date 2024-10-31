package com.hudson.multitenancy.repository;

import com.hudson.multitenancy.model.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantEntityRepository extends JpaRepository<TenantEntity, Long> {

}
