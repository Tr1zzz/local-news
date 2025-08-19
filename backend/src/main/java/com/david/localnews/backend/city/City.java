package com.david.localnews.backend.city;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "city", indexes = {
        @Index(name = "idx_city_name", columnList = "name"),
        @Index(name = "idx_city_state", columnList = "stateId")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2, nullable = false)
    private String stateId;

    @Column(nullable = false)
    private String stateName;

    private Double lat;
    private Double lon;

    private Integer population;
}
