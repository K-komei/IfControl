package ko.ifControl.batch.task;

import java.util.Map;

import ko.ifControl.batch.service.BatchJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchDispatcher {

    private final Map<String, BatchJobService> jobMap;
    private final TaskExecutor batchTaskExecutor;

    public BatchDispatcher(
            Map<String, BatchJobService> jobMap,
            @Qualifier("batchTaskExecutor") TaskExecutor batchTaskExecutor) {
        this.jobMap = Map.copyOf(jobMap);
        this.batchTaskExecutor = batchTaskExecutor;
    }

    public void dispatch(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("jobId must not be blank");
        }

        BatchJobService job = jobMap.get(jobId);
        if (job == null) {
            throw new IllegalArgumentException("未定義のJob ID: " + jobId);
        }

        batchTaskExecutor.execute(() -> executeJob(jobId, job));
        log.info("Batch queued: {}", jobId);
    }

    private void executeJob(String jobId, BatchJobService job) {
        log.info("Batch started: {}", jobId);
        try {
            job.execute();
            log.info("Batch completed: {}", jobId);
        } catch (RuntimeException exception) {
            log.error("Batch failed: {}", jobId, exception);
        }
    }
}