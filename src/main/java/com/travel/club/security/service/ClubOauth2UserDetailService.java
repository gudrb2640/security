package com.travel.club.security.service;

import com.travel.club.entity.ClubMember;
import com.travel.club.entity.ClubMemberRole;
import com.travel.club.repository.ClubMemberRepository;
import com.travel.club.security.dto.ClubAuthMemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ClubOauth2UserDetailService extends DefaultOAuth2UserService {

    private final ClubMemberRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("--------------------------------");
        log.info("userRequest: " + userRequest);

        String clientName = userRequest.getClientRegistration().getClientName();
        log.info("--------------------------------");
        log.info("clientName: " + clientName);
        log.info(userRequest.getAdditionalParameters());

        log.info("--------------------------------");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        oAuth2User.getAttributes().forEach((k,v) ->{
            log.info(k + ":" + v);
        });

        String email = null;

        if (clientName.equals("Google")) {
            email = oAuth2User.getAttribute("email");
        }
//        log.info(email);
//
//        ClubMember member = saveSocialMember(email);
//        return oAuth2User;
        ClubMember member = saveSocialMember(email);

        ClubAuthMemberDTO clubAuthMemberDTO = new ClubAuthMemberDTO(
                member.getEmail(),
                member.getPassword(),
                true,
                member.getRoleSet().stream().map(
                        role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toList()),
                oAuth2User.getAttributes()
        );

        clubAuthMemberDTO.setName(member.getName());

        return clubAuthMemberDTO;
    }

    private ClubMember saveSocialMember(String email) {

        //기존에 동일한 이메일로 가입한 회원이 있는 경우에는 그대로 조회만
        Optional<ClubMember> result = repository.findByEmail(email, true);

        if (result.isPresent()) {
            return result.get();
        }

        //없당면 회원 추가 패스워드는 1111 이름은 그냥 이메일 주소로
        ClubMember clubMember = ClubMember.builder()
                .email(email)
                .name(email)
                .password(passwordEncoder.encode("1111"))
                .fromSocial(true)
                .build();

        clubMember.addMemberRole(ClubMemberRole.USER);
        log.info(clubMember);

        return clubMember;

    }
}
