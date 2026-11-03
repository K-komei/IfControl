package ko.ifControll.base.controller;

import java.util.Map;

/** Shared contract for individual IF-call controllers. */
public interface ApiCallBaseController {
    String callIfLoad(Map<String, Object> paramMap);
}