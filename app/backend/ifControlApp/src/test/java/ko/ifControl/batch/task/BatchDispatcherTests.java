package ko.ifControl.batch.task;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import ko.ifControl.batch.service.OtherBatchService;
import ko.ifControl.batch.service.SalesDataBatchService;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.junit.jupiter.api.Test;

class BatchDispatcherTests {

    private final SalesDataBatchService salesDataBatchService = mock(SalesDataBatchService.class);
    private final OtherBatchService otherBatchService = mock(OtherBatchService.class);
    private final BatchDispatcher batchDispatcher = new BatchDispatcher(Map.of(
            "SALES_IMPORT", salesDataBatchService,
            "USER_UPDATE", otherBatchService),
            new SyncTaskExecutor());

    @Test
    void dispatchesSalesImportToSalesService() {
        batchDispatcher.dispatch("SALES_IMPORT");

        verify(salesDataBatchService).execute();
        verifyNoInteractions(otherBatchService);
    }

    @Test
    void dispatchesUserUpdateToOtherService() {
        batchDispatcher.dispatch("USER_UPDATE");

        verify(otherBatchService).execute();
        verifyNoInteractions(salesDataBatchService);
    }

    @Test
    void rejectsUnknownJobId() {
        assertThrows(IllegalArgumentException.class, () -> batchDispatcher.dispatch("UNKNOWN"));

        verifyNoInteractions(salesDataBatchService, otherBatchService);
    }

    @Test
    void rejectsBlankJobId() {
        assertThrows(IllegalArgumentException.class, () -> batchDispatcher.dispatch(" "));

        verifyNoInteractions(salesDataBatchService, otherBatchService);
    }

    @Test
    void queuesJobInsteadOfExecutingInline() {
        AtomicReference<Runnable> queuedTask = new AtomicReference<>();
        TaskExecutor queueingExecutor = queuedTask::set;
        BatchDispatcher dispatcher = new BatchDispatcher(Map.of(
                "SALES_IMPORT", salesDataBatchService,
                "USER_UPDATE", otherBatchService),
                queueingExecutor);

        dispatcher.dispatch("SALES_IMPORT");

        assertNotNull(queuedTask.get());
        verifyNoInteractions(salesDataBatchService, otherBatchService);

        queuedTask.get().run();

        verify(salesDataBatchService).execute();
        verifyNoInteractions(otherBatchService);
    }
}