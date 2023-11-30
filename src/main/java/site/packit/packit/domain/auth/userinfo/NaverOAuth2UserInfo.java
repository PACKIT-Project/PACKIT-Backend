package site.packit.packit.domain.auth.userinfo;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getOpenId() {
        Map<?, ?> response = (Map<?, ?>) attributes.get("response");

//        validateResponse(response);
        if (response == null) {
            return null;
        }

        return response.get("id")
                .toString();
    }

//    private void validateResponse(Map<?, ?> response) {
//        if (response == null) {
//            throw
//        }
//    }
}
