package ko.ifControl.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("SALES_IMPORT")
@Slf4j
public class SalesDataBatchService implements BatchJobService {

    @Override
    public void execute() {
        // TODO: Implement TNP_URAG_DATA validation and sales-data import.
        log.info("Sales-data batch invoked; import logic is not implemented yet.");
    }
}