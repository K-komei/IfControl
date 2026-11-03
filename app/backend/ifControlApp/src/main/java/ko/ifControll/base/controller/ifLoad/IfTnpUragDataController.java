package ko.ifControll.base.controller.ifLoad;

import ko.ifControll.base.controller.ApiCallBaseController;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/call/ifLoad/tnp-urag-data")
public class IfTnpUragDataController implements ApiCallBaseController {

    @PostMapping
    @Override
    public String callIfLoad(@RequestBody Map<String, Object> paramMap) {
        // Implement the logic for handling the ifLoad API call here
        // For example, you can process the paramMap and return a response
        return "Response from IfTnpUragData";
    }
}
