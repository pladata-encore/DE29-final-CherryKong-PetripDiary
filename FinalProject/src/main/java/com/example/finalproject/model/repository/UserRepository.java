package com.example.finalproject.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.finalproject.model.entity.UserEntity;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    
    @Query(value = "SELECT * FROM user WHERE userid = :userid", nativeQuery = true)
    public UserEntity getUserById(@Param("userid") String name);

    Optional<UserEntity> findByUserid(String userid);

}
