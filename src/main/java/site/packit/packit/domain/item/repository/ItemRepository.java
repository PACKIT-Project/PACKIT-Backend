package site.packit.packit.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.item.entity.Item;

import java.util.List;

public interface ItemRepository
        extends JpaRepository<Item, Long> {

    List<Item> findByCategory(Category category);

}
