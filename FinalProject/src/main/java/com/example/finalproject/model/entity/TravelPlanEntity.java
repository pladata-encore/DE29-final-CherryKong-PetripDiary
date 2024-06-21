package com.example.finalproject.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Entity(name =  "CourseEntity")
@Table(name = "course")
public class TravelPlanEntity {
    
    @Id    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseid;

    @NotBlank
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String courses;

    @NotBlank
    private String coursetitle;

    private String time;
    
    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private UserEntity userid;
}