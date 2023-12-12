package site.packit.packit.domain.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.domain.travel.entity.TravelMember;

import java.util.List;

public interface TravelMemberRepository extends JpaRepository<TravelMember, Long> {

    long countByTravel(Travel travel);

    boolean existsByTravelAndMember(Travel travel, Member member);
    List<TravelMember> findByTravel(Travel travel);

    List<TravelMember> findByMemberId(Long memberId);
}
