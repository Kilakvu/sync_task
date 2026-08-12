package org.dataki.domain.port.output;

import org.dataki.domain.model.Task;

/**
 * Puerto de salida que define cómo procesar tareas
 */
public interface TaskProcessor {
    void processTask(Task task);

    void scheduleTask(Task task);
}

