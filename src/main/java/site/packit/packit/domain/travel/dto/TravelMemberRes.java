package site.packit.packit.domain.travel.dto;

public record TravelMemberRes(
        Long memberId,
        String nickName,
        String profileImg,
        int checkedNum,
        int unCheckedNum
) {
}
