package com.example.finalproject.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Entity(name = "UserEntity")
@Table(name = "user")
public class UserEntity {
    @Id
    @NotNull
    private String userid;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
    private String role;
    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean isLogin;

    @OneToOne(mappedBy = "user") 
    private DogEntity dog; 
    
}