package site.packit.packit.domain.checkList.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.global.audit.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "checklist")
@Entity
public class CheckList
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer listOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id")
    @JsonIgnore
    private Travel travel;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean essential;

    @Builder
    public CheckList(
            String title,
            Integer listOrder,
            Travel travel,
            Boolean essential
    ) {
        this.title = title;
        this.listOrder = listOrder;
        this.travel = travel;
        this.essential = essential;
    }

    public void setListOrder(
            Integer listOrder
    ) {
        this.listOrder = listOrder;
    }

    public void updateListTitle(
            String title
    ) {
        this.title = title;
    }
}
