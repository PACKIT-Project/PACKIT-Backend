package site.packit.packit.domain.item.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.category.entity.Category;
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
    private int listOrder;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isChecked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @Builder
    public Item(
            String title,
            Integer listOrder,
            Boolean isChecked,
            Category category
    ) {
        this.title = title;
        this.listOrder = listOrder;
        this.isChecked = isChecked;
        this.category = category;
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
