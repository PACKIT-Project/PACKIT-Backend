package site.packit.packit.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.packit.packit.domain.checkList.entity.CheckList;
import site.packit.packit.domain.item.entity.Item;

import java.util.List;

public interface ItemRepository
        extends JpaRepository<Item, Long> {

    @Query("SELECT COALESCE(MAX(c.listOrder), 0) FROM Item c WHERE c.checkList = :checkList")
    Integer findMaxListOrderByCheckList(CheckList checkList);

    List<Item> findByCheckListId(Long checklistId);

    List<Item> findByCheckListAndListOrderGreaterThan(CheckList checkList, Integer listOrder);

    List<Item> findByCheckListIdOrderByListOrderAsc(Long checkListId);


}
