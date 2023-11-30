package site.packit.packit.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteAllByMemberPersonalId(String memberPersonalId);

    boolean existsByValue(String tokenValue);

    Optional<RefreshToken> findByValue(String refreshTokenValue);
}
