package com.example.userservice.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface UserRepository extends CrudRepository<UserEntity, Long> { // <데이터베이스랑 연결될 수 있는 Entity 정보, 기본키-클래스 타입>
}
