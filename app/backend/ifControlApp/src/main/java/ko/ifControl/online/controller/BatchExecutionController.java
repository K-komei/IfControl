package ko.ifControl.online.controller;

import java.util.Map;

import ko.ifControl.batch.task.BatchDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchExecutionController {

    private final BatchDispatcher batchDispatcher;

    @PostMapping("/execute")
    public ResponseEntity<String> executeBatch(@RequestBody Map<String, String> request) {
        String jobId = request == null ? null : request.get("jobId");

        try {
            batchDispatcher.dispatch(jobId);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        } catch (TaskRejectedException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Batch queue is full; retry later.",
                    exception);
        }

        return ResponseEntity.accepted().body("Job " + jobId + " accepted.");
    }
}