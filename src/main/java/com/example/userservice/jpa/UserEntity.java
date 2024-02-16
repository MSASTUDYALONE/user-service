package com.example.userservice.jpa;

import lombok.Data;

import javax.persistence.*;



@Data
@Entity
@Table(name = "users") // 빨간 밑줄은 실제 데이터베이스에서 사용할 수 있는 데이터 소스가 등록되지 않았기 때문이다. 무시해라..
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    private String encryptedPwd;
}
