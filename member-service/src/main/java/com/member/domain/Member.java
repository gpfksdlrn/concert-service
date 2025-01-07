package com.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "MEMBERS")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address1;

    @Column(nullable = false)
    private String address2;

    @Column(nullable = false)
    private Long balance = 0L; // 기본 잔액 설정

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role = UserRoleEnum.USER;

    public void chargePoint(Long chargePoint) {
        this.balance += chargePoint;
    }

    public enum UserRoleEnum {
        USER, ADMIN
    }
}
