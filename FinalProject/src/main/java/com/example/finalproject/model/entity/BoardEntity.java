package com.example.finalproject.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Entity(name = "BoardEntity")
@Table(name = "board")
public class BoardEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardid;

    @NotBlank
    private String title;
    
    @NotBlank
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String text;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private UserEntity userid;

    private String nickname;

    private String dogname;

    private String breed;

    private LocalDate dogbirth;

    private float dogweight;

    private String dogphotoUrl;

    private String dogphotoUrl2;

    private String dogphotoUrl3;

    private String dogphotoUrl4;

    private LocalDateTime time;

    private int likes; // 좋아요 수

    private int views;

    private Set<String> likedUsers = new HashSet<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentsEntity> comments = new HashSet<>();

}
