package site.packit.packit.domain.cluster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static site.packit.packit.domain.cluster.execption.ClusterErrorCode.CLUSTER_NOT_FOUND;
import static site.packit.packit.domain.travel.exception.TravelErrorCode.TRAVEL_NOT_FOUND;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {
    List<Cluster> findByTravelAndMember(Travel travel, Member member);
    List<Cluster> findByTravel(Travel travel);

    @Query("SELECT COALESCE(MAX(c.listOrder), 0) FROM Cluster c WHERE c.travel = :travel AND c.member = :member")
    Optional<Integer> findMaxListOrderByTravelAndMember(@Param("travel") Travel travel, @Param("member") Member member);

    default Cluster findByIdOrThrow(Long clusterId){
        return findById(clusterId)
                .orElseThrow(()-> new ResourceNotFoundException(CLUSTER_NOT_FOUND));
    }

    Optional<Cluster> findByIdAndMember(Long clusterId, Member member);

}

