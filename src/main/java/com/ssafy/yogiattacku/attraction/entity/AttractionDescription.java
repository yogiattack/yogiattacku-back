package com.ssafy.yogiattacku.attraction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attraction_description")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttractionDescription {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "sido_name", nullable = false)
    private String sidoName;

    @Column(name = "gugun_name", nullable = false)
    private String gugunName;

    @Column(name = "content_type_name", nullable = false)
    private String contentTypeName;

    @Column(name = "attraction_name", nullable = false)
    private String attractionName;

    @Column(name = "latitude", nullable = false, columnDefinition = "double precision")
    private Double latitude;

    @Column(name = "longitude", nullable = false, columnDefinition = "double precision")
    private Double longitude;

    @Column(name = "address", nullable = false)
    private String address;
}
