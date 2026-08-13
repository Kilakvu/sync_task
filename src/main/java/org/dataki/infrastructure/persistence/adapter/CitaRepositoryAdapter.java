package org.dataki.infrastructure.persistence.adapter;

import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaStatus;
import org.dataki.domain.port.output.CitaRepository;
import org.dataki.infrastructure.persistence.entity.CitaEntity;
import org.dataki.infrastructure.persistence.mapper.CitaEntityMapper;
import org.dataki.infrastructure.persistence.repository.CitaJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia que implementa el puerto CitaRepository
 */
@Component
public class CitaRepositoryAdapter implements CitaRepository {
    private final CitaJpaRepository jpaRepository;
    private final CitaEntityMapper mapper;

    public CitaRepositoryAdapter(CitaJpaRepository jpaRepository, CitaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cita save(Cita cita) {
        CitaEntity entity = mapper.toEntity(cita);
        CitaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Cita> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Cita> findByStatus(CitaStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Cita> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Cita> findPendingCitas() {
        return jpaRepository.findPendingCitasOrderByPriority()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Cita> findByScheduledForBetween(LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByScheduledForBetweenOrderByScheduledForAsc(start, end)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }
}
