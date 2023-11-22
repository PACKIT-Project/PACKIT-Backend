package site.packit.packit.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.member.constant.AccountStatus;
import site.packit.packit.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPersonalId(String personalId);

    Optional<Member> findByPersonalIdAndAccountStatus(String personalId, AccountStatus accountStatus);
}
