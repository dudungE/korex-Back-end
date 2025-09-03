package com.project.korex.externalAccount.entity;

import com.project.korex.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank")
@Getter
@Setter
@NoArgsConstructor
public class Bank extends BaseEntity {
    @Id
    @Column(length = 3)
    private String bankCode; // '004', '088' 등

    @Column(nullable = false, length = 50)
    private String bankName; // '국민은행', '신한은행' 등

//    @Column
//    private String bankLogo;

    @Column(nullable = false)
    private Boolean isActive = true;
}

