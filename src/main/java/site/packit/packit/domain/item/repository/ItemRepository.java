package site.packit.packit.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.item.entity.Item;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static site.packit.packit.domain.item.exception.ItemErrorCode.ITEM_NOT_FOUND;
import static site.packit.packit.domain.travel.exception.TravelErrorCode.TRAVEL_NOT_FOUND;

public interface ItemRepository
        extends JpaRepository<Item, Long> {

    List<Item> findByCategory(Category category);

    @Query("SELECT COALESCE(MAX(c.listOrder), 0) FROM Item c WHERE c.category = :category")
    Optional<Integer> findMaxListOrderByCategory(@Param("category") Category category);


    default Item findByIdOrThrow(Long itemId){
        return findById(itemId)
                .orElseThrow(()-> new ResourceNotFoundException(ITEM_NOT_FOUND));
    }
}
