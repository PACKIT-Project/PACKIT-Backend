package site.packit.packit.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
