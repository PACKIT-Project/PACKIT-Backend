package site.packit.packit.domain.cluster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.cluster.entity.Cluster;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {
}

