package com.prac.Prac.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    @Column(length = 30 ,nullable = false)
    private String userName;

    @Column(length = 100,nullable = false)
    private String user_address;

    @Column(length = 1111,nullable = false)
    private String pw;

}
