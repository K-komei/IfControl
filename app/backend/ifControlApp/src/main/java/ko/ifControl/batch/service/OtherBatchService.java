package ko.ifControl.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("USER_UPDATE")
@Slf4j
public class OtherBatchService implements BatchJobService {

    @Override
    public void execute() {
        // TODO: Implement the user-update batch when its requirements are defined.
        log.info("User-update batch invoked; processing logic is not implemented yet.");
    }
}