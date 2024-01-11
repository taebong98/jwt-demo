package com.taebong.szs.service;

import com.taebong.szs.common.exception.ForbiddenException;
import com.taebong.szs.domain.UserService;
import com.taebong.szs.domain.repository.UserRepository;
import com.taebong.szs.domain.vo.AllowedUsers;
import com.taebong.szs.domain.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    AllowedUsers allowedUsers;
    
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    Map<String, String> allowedUsersMap;
    User validUser;
    User inValidUser;

    @BeforeEach
    void init() {
        allowedUsersMap = allowedUsersFixture();

        validUser = User.builder()
                .id(1L)
                .userId("hong12")
                .password("123456")
                .name("홍길동")
                .regNo("860824-1655068")
                .build();

        inValidUser = User.builder()
                .id(1L)
                .userId("kim")
                .password("123456")
                .name("김태현")
                .regNo("860824-1111111")
                .build();
    }

    @Test
    public void 정해진_사용자가_아니면_회원가입_불가능() throws Exception {
        assertThrows(ForbiddenException.class, () -> {
            userService.signup(inValidUser);
        });
    }

    @Test
    public void 정해진_사용자면_회원가입_가능() throws Exception {
        // given
        when(allowedUsers.getAllowedUserMap()).thenReturn(allowedUsersMap);

        // when
        userService.signup(validUser);

        // then
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void 비밀번호_암호화_안돼_있다면_예외발생() throws Exception {
        assertThrows(RuntimeException.class, () -> userService.signup(validUser));
    }

    @Test
    public void 비밀번호_암호화_되어있다면_정상동작() throws Exception {
        // given
        when(allowedUsers.getAllowedUserMap()).thenReturn(allowedUsersMap);
        String encodedPassword = "hashedPassword";
        when(passwordEncoder.encode(validUser.getPassword())).thenReturn(encodedPassword);

        // when
        assertDoesNotThrow(() -> userService.signup(validUser));

        // then
        verify(userRepository, times(1)).save(any());
    }

    private Map<String, String> allowedUsersFixture() {
        Map<String, String> userMap = new HashMap<>();

        userMap.put("860824-1655068", "홍길동");
        userMap.put("921108-1582816", "김둘리");
        userMap.put("880601-2455116", "마징가");
        userMap.put("910411-1656116", "베지터");
        userMap.put("820326-2715702", "손오공");

        return userMap;
    }
}
