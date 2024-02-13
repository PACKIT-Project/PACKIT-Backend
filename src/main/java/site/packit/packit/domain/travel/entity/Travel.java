package site.packit.packit.domain.travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.destination.entity.Destination;
import site.packit.packit.domain.item.entity.Item;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.global.audit.BaseEntity;

import java.time.LocalDateTime;
import java.util.*;

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

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String invitationCode;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cluster> clusters = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private Member owner;

    @Builder
    public Travel(
            String title,
            Destination destination,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Member owner,
            String invitationCode
    ) {
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.owner = owner;
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

    public void addCluster(Cluster cluster) {
        this.clusters.add(cluster);
    }

}
