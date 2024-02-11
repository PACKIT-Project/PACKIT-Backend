package site.packit.packit.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.notification.entity.FcmToken;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findFcmTokenByMember(Member member);
}
