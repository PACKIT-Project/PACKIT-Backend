package site.packit.packit.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.destination.entity.Destination;
import site.packit.packit.domain.member.constant.AccountStatus;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.util.Optional;

import static site.packit.packit.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static site.packit.packit.domain.travel.exception.TravelErrorCode.DESTINATION_NOT_FOUND;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPersonalId(String personalId);
    Optional<Member> findByIdAndAccountStatus(Long memberId, AccountStatus accountStatus);

    boolean existsByNickname(String nickname);

    default Member findByIdOrThrow(Long memberId){
        return findById(memberId)
                .orElseThrow(()-> new ResourceNotFoundException(MEMBER_NOT_FOUND));
    }
}
