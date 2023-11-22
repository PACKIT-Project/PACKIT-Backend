package site.packit.packit.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.member.dto.request.RegisterRequest;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.exception.MemberException;
import site.packit.packit.domain.member.repository.MemberRepository;

import static site.packit.packit.domain.member.constant.AccountStatus.WAITING_TO_JOIN;
import static site.packit.packit.domain.member.exception.MemberErrorCode.TEMP_MEMBER_NOT_FOUND;

@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long register(String memberPersonalId, RegisterRequest request) {
        Member tempMember = memberRepository.findByPersonalIdAndAccountStatus(memberPersonalId, WAITING_TO_JOIN)
                .orElseThrow(() -> new MemberException(TEMP_MEMBER_NOT_FOUND));
        tempMember.register(request.nickname(), request.profileImageUrl());

        return tempMember.getId();
    }
}
