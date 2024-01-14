package com.taebong.szs.service;

import com.taebong.szs.common.exception.DataNotFoundException;
import com.taebong.szs.common.exception.ForbiddenException;
import com.taebong.szs.common.exception.LoginException;
import com.taebong.szs.common.jwt.JwtTokenProvider;
import com.taebong.szs.common.util.CryptUtils;
import com.taebong.szs.controller.dto.LoginDto;
import com.taebong.szs.domain.UserService;
import com.taebong.szs.domain.repository.UserRepository;
import com.taebong.szs.domain.vo.AllowedUsers;
import com.taebong.szs.domain.vo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    CryptUtils cryptUtils;

    @Mock
    JwtTokenProvider jwtTokenProvider;

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
        when(cryptUtils.encrypt(validUser.getPassword())).thenReturn(encodedPassword);

        // when
        assertDoesNotThrow(() -> userService.signup(validUser));

        // then
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void 로그인_회원_정보가_일치한다면_토큰을_발급한다 () throws Exception {
        // given
        LoginDto loginDto = LoginDto.builder().userId("hong12").password("123456").build();

        // when
        when(userRepository.findByUserId(loginDto.getUserId())).thenReturn(Optional.of(validUser));
        when(cryptUtils.matches(loginDto.getPassword(), validUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(validUser)).thenReturn("fakeToken");

        // then
        String token = userService.login(loginDto);

        assertThat("fakeToken").isEqualTo(token);
    }

    @Test
    public void 로그인_회원_정보가_조회되지_않는다면_예외를_발생시킨다() throws Exception {
        // given
        LoginDto loginDto = LoginDto.builder().userId("be").password("123456").build();

        // when
        when(userRepository.findByUserId(loginDto.getUserId())).thenReturn(Optional.empty());

        // then
        assertThrows(DataNotFoundException.class, () -> userService.login(loginDto));
        verify(userRepository, times(1)).findByUserId("be");
    }

    @Test
    public void 로그인_비멀번호가_일치하지_않다면_예외를_발생시킨다() throws Exception {
        LoginDto loginDto = LoginDto.builder().userId("hong12").password("일치하지 않는 비밀번호").build();

        when(userRepository.findByUserId(loginDto.getUserId())).thenReturn(Optional.of(validUser));
        when(cryptUtils.matches(loginDto.getPassword(), validUser.getPassword())).thenReturn(false);

        // then
        assertThrows(LoginException.class, () -> userService.login(loginDto));
    }

    @Test
    public void 토큰_생성_검증() throws Exception {
        // given
        String token = generateTestToken(validUser.getName(), validUser.getUserId());
        Claims claims = Jwts.claims();
        claims.put("name", validUser.getName());
        claims.put("userId", validUser.getUserId());
        when(jwtTokenProvider.getClaims(token)).thenReturn(claims);

        // when
        User result = userService.getUserInJwtToken(token);

        // then
        assertThat(validUser.getName()).isEqualTo(result.getName());
        assertThat(validUser.getUserId()).isEqualTo(result.getUserId());
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

    private String generateTestToken(String name, String userId) {
        return Jwts.builder()
                .claim("name", name)
                .claim("userId", userId)
                .compact();
    }
}
