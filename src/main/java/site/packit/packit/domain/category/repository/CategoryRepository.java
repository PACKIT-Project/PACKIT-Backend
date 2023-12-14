package site.packit.packit.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.travel.entity.Travel;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCluster(Cluster cluster);
    @Query("SELECT COALESCE(MAX(c.listOrder), 0) FROM Category c WHERE c.cluster = :cluster")
    Optional<Integer> findMaxListOrderByCluster(@Param("cluster") Cluster cluster);

}
