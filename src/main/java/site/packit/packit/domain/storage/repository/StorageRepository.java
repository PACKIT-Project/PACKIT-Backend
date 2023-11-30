package site.packit.packit.domain.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.storage.entity.Storage;
import site.packit.packit.domain.travel.entity.Travel;

import java.util.List;

public interface StorageRepository
        extends JpaRepository<Storage, Long> {

    Storage findByMemberAndTravel(Member member, Travel travel);

    List<Storage> findByMember(Member member);

    Boolean existsByMemberIdAndTravelId(Long memberId, Long travelId);

    Storage findByMemberIdAndTravelId(Long memberId, Long travelId);

}
