package com.taebong.szs.domain.vo;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AllowedUsers {
    public Map<String, String> getAllowedUserMap() {
        Map<String, String> userMap = new HashMap<>();

        userMap.put("860824-1655068", "홍길동");
        userMap.put("921108-1582816", "김둘리");
        userMap.put("880601-2455116", "마징가");
        userMap.put("910411-1656116", "베지터");
        userMap.put("820326-2715702", "손오공");

        return userMap;
    }
}
