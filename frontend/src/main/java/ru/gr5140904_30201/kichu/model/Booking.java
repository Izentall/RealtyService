package ru.gr5140904_30201.kichu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.gr5140904_30201.kichu.model.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @JsonProperty
    private Long id;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private Long propertyId;
    @JsonProperty
    private LocalDate startDate;
    @JsonProperty
    private LocalDate endDate;
    @JsonProperty
    private BookingStatus status;
    @JsonProperty
    private LocalDateTime createdAt;
}
