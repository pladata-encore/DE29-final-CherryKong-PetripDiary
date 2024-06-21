package com.example.finalproject.model.entity;


import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(name = "DogEntity")
@Table(name = "dog")
public class DogEntity {
    
    @Id
    @NotNull
    private String userid;
    @NotNull
    private String dogname;
    private LocalDate dogbirth;
    private float dogweight;
    
    private String dogphotoUrl;


    @OneToOne
    @MapsId
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private UserEntity user;

}