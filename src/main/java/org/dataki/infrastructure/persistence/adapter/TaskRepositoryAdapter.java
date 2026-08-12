package org.dataki.infrastructure.persistence.adapter;

import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskStatus;
import org.dataki.domain.port.output.TaskRepository;
import org.dataki.infrastructure.persistence.entity.TaskEntity;
import org.dataki.infrastructure.persistence.mapper.TaskEntityMapper;
import org.dataki.infrastructure.persistence.repository.TaskJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia que implementa el puerto TaskRepository
 */
@Component
public class TaskRepositoryAdapter implements TaskRepository {
    private final TaskJpaRepository jpaRepository;
    private final TaskEntityMapper mapper;

    public TaskRepositoryAdapter(TaskJpaRepository jpaRepository, TaskEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = mapper.toEntity(task);
        TaskEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Task> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> findPendingTasks() {
        return jpaRepository.findPendingTasksOrderByPriority()
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

