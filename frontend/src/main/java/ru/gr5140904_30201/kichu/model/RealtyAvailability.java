package ru.gr5140904_30201.kichu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtyAvailability {
    @JsonProperty
    private List<DatesFromTo> availableIntervals;
}
