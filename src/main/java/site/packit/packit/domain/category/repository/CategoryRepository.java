package site.packit.packit.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.cluster.entity.Cluster;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCluster(Cluster cluster);
}
