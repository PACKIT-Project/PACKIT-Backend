package site.packit.packit.domain.cluster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.travel.entity.Travel;

import java.util.List;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {
    List<Cluster> findByTravelAndMember(Travel travel, Member member);
    List<Cluster> findByTravel(Travel travel);


}

