package com.ssafy.moa2zi.member.application;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ssafy.moa2zi.common.storage.application.S3Service;
import com.ssafy.moa2zi.finance.application.FinanceAdminService;
import com.ssafy.moa2zi.common.util.AESUtil;
import com.ssafy.moa2zi.finance.application.FinanceMemberService;
import com.ssafy.moa2zi.friend.domain.FriendRepository;
import com.ssafy.moa2zi.member.domain.Disclosure;
import com.ssafy.moa2zi.member.dto.request.*;
import com.ssafy.moa2zi.member.dto.response.MemberGetByNicknameListResponse;
import com.ssafy.moa2zi.term.domain.*;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.moa2zi.common.storage.application.ImageUtil;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.domain.Role;
import com.ssafy.moa2zi.member.dto.response.MemberGetResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.webjars.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AESUtil aesUtil;
    private final ImageUtil imageUtil;
    private final S3Service s3Service;
    private final FinanceMemberService financeMemberService;
    private final FriendRepository friendRepository;

    @Value("${basic.image.profile-url}")
    private String basicProfileImageUrl;

    @Transactional
    public void createMember(MemberJoinRequest memberJoinRequest) throws Exception {
        // 중복 체크
        duplicationUsernameWithThrow(memberJoinRequest.username());
        duplicationNicknameWithThrow(memberJoinRequest.nickname());
        duplicationPhoneNumeberWithThow(memberJoinRequest.phoneNumber());

        Member member = Member.builder()
                .username(memberJoinRequest.username())
                .nickname(memberJoinRequest.nickname())
                .password(bCryptPasswordEncoder.encode(memberJoinRequest.password()))
                .birthday(memberJoinRequest.birthday())
                .gender(memberJoinRequest.gender())
                .role(Role.USER)
                .profileImage(memberJoinRequest.profileImage())
                .alarm(true)
                .disclosure(Disclosure.ONLY_ME)
                .phoneNumber(aesUtil.encrypt(memberJoinRequest.phoneNumber()))
                .build();

        memberRepository.save(member);

        validateTerms(memberJoinRequest.memberTermList());
        List<MemberTerm> memberTermList = memberJoinRequest.memberTermList().stream()
                .map(memberTerm -> MemberTerm.builder()
                        .termId(memberTerm.termId())
                        .memberId(member.getMemberId())
                        .agree(memberTerm.agree())
                        .build())
                .collect(Collectors.toList());

        memberTermRepository.saveAll(memberTermList);
        financeMemberService.generateFinUserKey(member);

    }


    private void validateTerms(List<MemberTermRequest> memberTermList) throws BadRequestException {
        for (MemberTermRequest memberTerm : memberTermList) {
            Term term = termRepository.findById(memberTerm.termId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 이용약관입니다."));

            if (term.getTermType().equals(TermType.MANDATORY) && !memberTerm.agree()) {
                throw new BadRequestException("필수 이용약관을 동의해야만 서비스를 이용하실 수 있습니다.");
            }
        }
    }

    @Transactional(readOnly = true)
    public MemberGetResponse getMember(Long memberId, CustomMemberDetails loginMember){
        Member member = findMember(memberId);

//        String imageUrl = null;
//        if(member.getProfileImage().startsWith("http")){
//            imageUrl = member.getProfileImage();
//        }
//        else{
//            imageUrl = s3Service.getPreSignedUrl(member.getProfileImage());
//        }
        Boolean theyAreFriend = true;
        if(!member.getMemberId().equals(loginMember.getMemberId())
                && !friendRepository.areTheyFriend(memberId, loginMember.getMemberId())){
            theyAreFriend = false;
        }

        return MemberGetResponse.builder()
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .gender(member.getGender())
                .profileImage(member.getProfileImage())
                .alarm(member.getAlarm())
                .disclosure(member.getDisclosure())
                .createdAt(member.getCreatedAt())
                .updateAt(member.getUpdatedAt())
                .theyAreFriend(theyAreFriend)
                .build();
    }

    @Transactional(readOnly = true)
    public MemberGetResponse getMember(CustomMemberDetails loginMember){
        return this.getMember(loginMember.getMemberId(), loginMember);
    }

    public MemberGetByNicknameListResponse getMemberByNickname(
        MemberGetByNicknameRequest memberGetByNicknameRequest,
        CustomMemberDetails loginMember
    ) throws BadRequestException {
        return memberRepository.getMemberByNickname(memberGetByNicknameRequest, loginMember.getMemberId());
    }


    public Member findMember(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
    }

    public boolean duplicateUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    public void duplicationUsernameWithThrow(String username){
        if(duplicateUsername(username)){
            throw new DuplicateKeyException("이미 계정이 존재합니다.");
        }
    }

    public void duplicationNicknameWithThrow(String nickname){
        if(duplicateNickname(nickname)){
            throw new DuplicateKeyException("이미 사용중인 닉네임입니다.");
        }
    }

    public void duplicationPhoneNumeberWithThow(String phoneNumber) throws Exception {
        if(!phoneNumber.startsWith("010")){
            throw new BadRequestException("유효하지않는 전화번호 입니다.");
        }

        String encryptedPhoneNumber = aesUtil.encrypt(phoneNumber);

        if(memberRepository.existsByPhoneNumber(encryptedPhoneNumber)){
            throw new DuplicateKeyException("이미 가입한 번호가 존재합니다.");
        }
    }


    // 닉네임 중복확인
    public boolean duplicateNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public void updateMember(
            MemberUpdateRequest memberUpdateRequest,
            CustomMemberDetails loginMember
    ){
        Member member = findMember(loginMember.getMemberId());


        if(!member.getNickname().equals(memberUpdateRequest.nickname())){
            duplicationNicknameWithThrow(memberUpdateRequest.nickname());
        }

        member.setNickname(memberUpdateRequest.nickname());
        member.setBirthday(memberUpdateRequest.birthday());
        member.setGender(memberUpdateRequest.gender());
        member.setProfileImage(memberUpdateRequest.profileImage());
        member.setAlarm(memberUpdateRequest.alarm());
        member.setDisclosure(memberUpdateRequest.disclosure());

    }

    // 4. 프로필 이미지 업데이트
    @Transactional
    public String updateProfileImage(
            Long memberId,
            MultipartFile multipartFile,
            CustomMemberDetails loginMember
    ) throws IOException {
        Member member = findMember(loginMember.getMemberId());

//        String newProfileImageUrl = imageUtil.store(multipartFile, "profile");
        String newProfileImageUrl = imageUtil.CovertToWebpAndStore(multipartFile, "profile");
        member.setProfileImage(newProfileImageUrl);
        memberRepository.save(member);
        return newProfileImageUrl;
    }

    private void validateMemberId(Long memberId1,
                                  Long memberId2 ) throws AccessDeniedException {
        if (!Objects.equals(memberId1, memberId2)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
    }

    @Transactional
    public void updatePassword(
            MemberPasswordUpdateRequest passwordUpdateRequest,
            CustomMemberDetails loginMember
    ) throws BadRequestException {
        Member member = findMember(loginMember.getMemberId());

        validatePassword(passwordUpdateRequest.oldPassword(), loginMember.getPassword());

        member.setPassword(bCryptPasswordEncoder.encode(passwordUpdateRequest.newPassword()));
        memberRepository.save(member);
    }

    private void validatePassword(String oldPassword, String nowPassword ) throws BadRequestException {
        if (!bCryptPasswordEncoder.matches(oldPassword, nowPassword)) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void deleteMember(CustomMemberDetails loginMember) {
        Member member = findMember(loginMember.getMemberId());
        memberRepository.delete(member);
    }

    @Transactional
    public void updateFCMToken(String token, Member member){
        member.setFcmToken(token);
    }


}