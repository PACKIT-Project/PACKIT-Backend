package site.packit.packit.domain.item.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.checkList.entity.CheckList;
import site.packit.packit.global.audit.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item")
@Entity
public class Item
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer listOrder;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isChecked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore
    private CheckList checkList;

    @Builder
    public Item(
            String title,
            Integer listOrder,
            Boolean isChecked,
            CheckList checkList
    ) {
        this.title = title;
        this.listOrder = listOrder;
        this.isChecked = isChecked;
        this.checkList = checkList;
    }

    public void setListOrder(
            Integer listOrder
    ) {
        this.listOrder = listOrder;
    }

    public void setChecked(Boolean checked) {
        this.isChecked = checked;
    }

    public void toggleChecked() {
        this.isChecked = !this.isChecked;
    }

    public void updateItemTitle(
            String title
    ){
        this.title = title;
    }

}
