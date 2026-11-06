package ko.ifControl.common;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class DateFormatComponent {

    public String format(TemporalAccessor temporalValue, String pattern) {
        if (temporalValue == null) {
            return null;
        }

        return DateTimeFormatter.ofPattern(pattern, Locale.ROOT).format(temporalValue);
    }
}