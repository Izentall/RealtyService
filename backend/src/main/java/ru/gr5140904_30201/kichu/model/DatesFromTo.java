package ru.gr5140904_30201.kichu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatesFromTo {
    @JsonProperty
    private LocalDate from;
    @JsonProperty
    private LocalDate to;
}
