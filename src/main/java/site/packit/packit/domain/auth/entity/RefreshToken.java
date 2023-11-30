package site.packit.packit.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.global.audit.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String value;

    @Column(length = 100, nullable = false, updatable = false)
    private String memberPersonalId;

    private RefreshToken(String value, String memberPersonalId
    ) {
        this.value = value;
        this.memberPersonalId = memberPersonalId;
    }

    public static RefreshToken of(String value, String memberPersonalId) {
        return new RefreshToken(value, memberPersonalId);
    }

    public void updateValue(String tokenValue) {
        this.value = tokenValue;
    }
}
