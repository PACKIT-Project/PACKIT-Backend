package site.packit.packit.domain.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.item.entity.Item;
import site.packit.packit.global.audit.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
@Entity
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private int listOrder;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id")
    @JsonIgnore
    private Cluster cluster;

    @Builder
    public Category(
            String title,
            int listOrder,
            Cluster cluster
    ){
        this.title = title;
        this.listOrder = listOrder;
        this.cluster = cluster;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

}
