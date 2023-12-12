package site.packit.packit.domain.travel.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.global.audit.BaseEntity;
import site.packit.packit.global.audit.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "travel_member")
@Entity
public class TravelMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public TravelMember(Travel travel, Member member) {
        this.travel = travel;
        this.member = member;
    }
}