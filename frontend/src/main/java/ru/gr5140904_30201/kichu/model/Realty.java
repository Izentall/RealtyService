package ru.gr5140904_30201.kichu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Realty {
    @JsonProperty
    private Long id;
    @JsonProperty
    private Long ownerId;
    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private String address;
    @JsonProperty
    private Long pricePerDay;
    @JsonProperty
    private RealtyAvailability availability;
}
