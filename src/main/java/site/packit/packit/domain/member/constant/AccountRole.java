package site.packit.packit.domain.member.constant;

public enum AccountRole {
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;

    AccountRole(String key, String title) {
        this.key = key;
        this.title = title;
    }
}
