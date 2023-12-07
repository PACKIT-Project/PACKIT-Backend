package site.packit.packit.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.member.constant.LoginProvider;
import site.packit.packit.domain.member.dto.MemberDto;
import site.packit.packit.domain.member.dto.request.UpdateMemberProfileRequest;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.exception.MemberErrorCode;
import site.packit.packit.domain.member.exception.MemberException;
import site.packit.packit.domain.member.repository.MemberRepository;

import static site.packit.packit.domain.member.constant.AccountStatus.WAITING_TO_JOIN;
import static site.packit.packit.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static site.packit.packit.domain.member.exception.MemberErrorCode.TEMP_MEMBER_NOT_FOUND;

@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member findMemberByPersonalIdOrCreateMember(String personalId, LoginProvider loginProvider) {
        return memberRepository.findByPersonalId(personalId)
                .filter(findMember -> findMember.validateLoginProvider(loginProvider))
                .orElseGet(() -> createTempMember(personalId, loginProvider));
    }

    private Member createTempMember(String personalId, LoginProvider loginProvider) {
        return memberRepository.save(Member.createTempUser(personalId, loginProvider));
    }

    public Long register(Long memberId, UpdateMemberProfileRequest request) {
        Member tempMember = getTempMember(memberId);
        tempMember.register(request.nickname(), request.profileImageUrl(), request.enableNotification(), request.checkTerms());

        return tempMember.getId();
    }

    private Member getTempMember(Long memberId) {
        return memberRepository.findByIdAndAccountStatus(memberId, WAITING_TO_JOIN)
                .orElseThrow(() -> new MemberException(TEMP_MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member getMember(String personalId) {
        return memberRepository.findByPersonalId(personalId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public MemberDto getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        return MemberDto.of(member);
    }

    public void updateMemberProfile(Long memberId, UpdateMemberProfileRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        member.updateMemberProfile(request.nickname(), request.profileImageUrl());
    }

    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        member.remove();
    }

    @Transactional(readOnly = true)
    public boolean checkMemberNicknameDuplicated(String memberNickname) {
        return memberRepository.existsByNickname(memberNickname);
    }

    public void enableNotification(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        member.enableNotification();
    }

    public void disableNotification(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        member.disableNotification();
    }
}
