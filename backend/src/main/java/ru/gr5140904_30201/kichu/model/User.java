package ru.gr5140904_30201.kichu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String email;
    @JsonProperty
    private String password;
    @JsonProperty
    private String userRole;
    @JsonProperty
    private String name;
}