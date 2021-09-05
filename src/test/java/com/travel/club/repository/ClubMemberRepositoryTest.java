package com.travel.club.repository;

import com.travel.club.entity.ClubMember;
import com.travel.club.entity.ClubMemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
class ClubMemberRepositoryTest {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void insertDummies() {

        //1 ~ 80 까지는 USER
        //80 ~90 까지는 USER, MANAGER
        //91~100 까지는 USER,MANAGER,ADMIN

        IntStream.rangeClosed(1,100).forEach(i ->{

            ClubMember clubMember = ClubMember.builder()
                    .email("user" +i + "@aaaa.com")
                    .name("사용자" + i)
                    .fromSocial(false)
                    .password(passwordEncoder.encode("1111"))
                    .build();

            //default role
            clubMember.addMemberRole(ClubMemberRole.USER);
            if (i > 80) {
                clubMember.addMemberRole(ClubMemberRole.MANAGER);
            }
            if (i > 90) {
                clubMember.addMemberRole(ClubMemberRole.ADMIN);
            }
            clubMemberRepository.save(clubMember);
        });
    }

    @Test
    void testRead() {
        Optional<ClubMember> result = clubMemberRepository.findByEmail("user95@naver.com", false);

        ClubMember clubMember = result.get();

        System.out.println(clubMember);
    }
}