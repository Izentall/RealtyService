package ru.gr5140904_30201.kichu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertySearchCriteria {
    @JsonProperty
    private String location;
    @JsonProperty
    private LocalDate startDate;
    @JsonProperty
    private LocalDate endDate;
    @JsonProperty
    private Long minPrice;
    @JsonProperty
    private Long maxPrice;
    @JsonProperty
    private String propertyType;
}

