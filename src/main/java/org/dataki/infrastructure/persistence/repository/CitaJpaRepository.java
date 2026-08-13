package org.dataki.infrastructure.persistence.repository;

import org.dataki.infrastructure.persistence.entity.CitaEntity;
import org.dataki.domain.model.CitaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para acceso a datos de citas
 */
@Repository
public interface CitaJpaRepository extends JpaRepository<CitaEntity, String> {
    List<CitaEntity> findByStatus(CitaStatus status);

    @Query("SELECT c FROM CitaEntity c WHERE c.status IN ('PENDING', 'RETRY') " +
            "AND (c.scheduledFor IS NULL OR c.scheduledFor <= CURRENT_TIMESTAMP) " +
            "ORDER BY c.priority ASC, c.createdAt ASC")
    List<CitaEntity> findPendingCitasOrderByPriority();

    @Query("SELECT c FROM CitaEntity c WHERE c.status = 'SCHEDULED' AND c.scheduledFor <= ?1 " +
            "ORDER BY c.scheduledFor ASC")
    List<CitaEntity> findScheduledCitasReadyToProcess(LocalDateTime now);

    List<CitaEntity> findByScheduledForBetweenOrderByScheduledForAsc(@Param("start") LocalDateTime start,
                                                                     @Param("end") LocalDateTime end);
}
