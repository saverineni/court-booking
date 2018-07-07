
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class SearchCriteria {

    private String searchTerm;
    private List<String> originCountryCode;
    private List<String> dispatchCountryCode;
    private LocalDate entryDateFrom;
    private LocalDate entryDateTo;

    public SearchCriteria(String searchTerm, List<String> originCountryCode, List<String> dispatchCountryCode) {
        this.searchTerm = searchTerm;
        this.originCountryCode = originCountryCode;
        this.dispatchCountryCode = dispatchCountryCode;
    }


    public Optional<String> optionalSearchTerm() {
        return Optional.ofNullable(searchTerm)
                .map(String::trim)
                .filter(t -> !t.isEmpty());
    }

    public List<String> getOriginCountryCode() {
        if (originCountryCode == null) {
            return emptyList();
        }
        if (originCountryCode.isEmpty()) {
            // Spring converts a single empty string parameter to an empty list
            // - there is no easy hook to override this default behaviour
            return singletonList("");
        }
        return originCountryCode;
    }

    public List<String> getDispatchCountryCode() {
        if (dispatchCountryCode == null) {
            return emptyList();
        }
        if (dispatchCountryCode.isEmpty()) {
            // Spring converts a single empty string parameter to an empty list
            // - there is no easy hook to override this default behaviour
            return singletonList("");
        }
        return dispatchCountryCode;
    }

    public Optional<LocalDateTime> optionalEntryDateTimeFrom() {
        return Optional.ofNullable(entryDateFrom)
                .map(date -> LocalDateTime.of(date, LocalTime.MIN));
    }

    public Optional<LocalDateTime> optionalEntryDateTimeTo() {
        return Optional.ofNullable(entryDateTo)
                .map(date -> LocalDateTime.of(date, LocalTime.MAX));
    }
}
