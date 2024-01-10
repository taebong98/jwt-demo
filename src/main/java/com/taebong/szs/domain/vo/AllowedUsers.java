package com.taebong.szs.domain.vo;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllowedUsers {
    public List<User> getAllowedUserList() {
        return List.of(
                User.builder().name("홍길동").regNo("860824-1655068").build(),
                User.builder().name("김둘리").regNo("921108-1582816").build(),
                User.builder().name("마징가").regNo("880601-2455116").build(),
                User.builder().name("베지터").regNo("910411-1656116").build(),
                User.builder().name("손오공").regNo("820326-2715702").build()
        );
    }
}
