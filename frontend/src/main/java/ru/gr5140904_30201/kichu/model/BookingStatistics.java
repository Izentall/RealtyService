package ru.gr5140904_30201.kichu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatistics {
    @JsonProperty
    private Long propertyId;
    @JsonProperty
    private Long totalBookings;
    @JsonProperty
    private Long totalDaysBooked;
}
