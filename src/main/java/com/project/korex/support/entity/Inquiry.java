package com.project.korex.support.entity;

import com.project.korex.common.BaseEntity;
import com.project.korex.support.enums.InquiryCategory;
import com.project.korex.support.enums.InquiryStatus;
import com.project.korex.user.entity.Users;
import io.lettuce.core.KillArgs;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Check(constraints = "status in ('REGISTERED','ANSWERED','WITHDRAW')")
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="inquiry_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private InquiryCategory category;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private InquiryStatus status = InquiryStatus.REGISTERED;

    @Builder
    public static Inquiry of(Users user, InquiryCategory category, String title, String content, Boolean deleted) {
        Inquiry inq = new Inquiry();
        inq.user = user;
        inq.category = category;
        inq.title = title;
        inq.content = content;
        inq.deleted = deleted;
        inq.status = InquiryStatus.REGISTERED;
        return inq;
    }
}
