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

    /** Пример: "New York" */
    @Column(nullable = false)
    private String name;

    /** Двухбуквенный код штата: NY, CA, TX... */
    @Column(length = 2, nullable = false)
    private String stateId;

    /** Полное имя штата: "New York" */
    @Column(nullable = false)
    private String stateName;

    private Double lat;
    private Double lon;

    /** Можно null, если у источника нет населения */
    private Integer population;
}
