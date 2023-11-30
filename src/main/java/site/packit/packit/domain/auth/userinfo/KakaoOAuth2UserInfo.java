package site.packit.packit.domain.auth.userinfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getOpenId() {
        return attributes.get("id")
                .toString();
    }
}
