package com.prac.Prac.Repository;

import com.prac.Prac.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//UserEntity Primarykey는 Integer
public interface UserRepository extends JpaRepository<UserEntity,Integer> {
  UserEntity findByUserName(String username);
}
