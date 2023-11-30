package site.packit.packit.domain.travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.travel.constant.DestinationType;
import site.packit.packit.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "travel")
@Entity
public class Travel
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private DestinationType destinationType;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Builder
    public Travel(
            String title,
            DestinationType destinationType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Member member
    ) {
        this.title = title;
        this.destinationType = destinationType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.member = member;
    }

    public void updateTravel(
            String title,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
