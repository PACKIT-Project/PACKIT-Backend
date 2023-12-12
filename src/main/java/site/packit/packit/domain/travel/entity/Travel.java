package site.packit.packit.domain.travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.destination.entity.Destination;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.global.audit.BaseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String invitationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private Member owner;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TravelMember> travelMembers = new HashSet<>();

    @Builder
    public Travel(
            String title,
            Destination destination,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Member member,
            String invitationCode
    ) {
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.owner = member;
        this.invitationCode = invitationCode;
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

    public Set<TravelMember> getTravelMembers() {
        return Collections.unmodifiableSet(travelMembers);
    }
}
