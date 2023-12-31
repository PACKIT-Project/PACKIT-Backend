package site.packit.packit.domain.cluster.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.global.audit.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cluster")
@Entity
public class Cluster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer listOrder;

    @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id")
    @JsonIgnore
    private Travel travel;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Cluster(
            String title,
            int listOrder,
            Travel travel,
            Member member
    ){
        this.title = title;
        this.listOrder = listOrder;
        this.travel = travel;
        this.member = member;
    }

    public void setListOrder(Integer listOrder) {
        this.listOrder = listOrder;
    }
    public void addCategory(Category category) {
        this.categories.add(category);
    }

}
