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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    AllowedUsers allowedUsers;

    @InjectMocks
    UserService userService;

    List<User> validUserList;
    User validUser;
    User inValidUser;

    @BeforeEach
    void init() {
        validUserList = validUserListFixture();

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
        Assertions.assertThrows(ForbiddenException.class, () -> {
            userService.signup(inValidUser);
        });
    }

    @Test
    public void 정해진_사용자면_회원가입_가능() throws Exception {
        // given
        List<User> allowedUserList = validUserListFixture();
        when(allowedUsers.getAllowedUserList()).thenReturn(allowedUserList);

        // when
        userService.signup(validUser);

        // then
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void 비밀번호_암호화_안돼있다면_예외발생() throws Exception {
        // given

        // when

        // then
    }

    @Test
    public void 주민등록번호_암호화_안돼있다면_예외발생() throws Exception {
        // given

        // when

        // then
    }

    @Test
    public void 회원가입_성공() throws Exception {
        // given

        // when

        // then
    }

    private List<User> validUserListFixture() {
        List<User> userList = new ArrayList<>();
        User user1 = new User((long) 1, "hong12", "123456", "홍길동", "860824-1655068");
        User user2 = new User((long) 2, "kim12", "123456", "김둘리", "921108-1582816");
        User user3 = new User((long) 3, "ma12", "123456", "마징가", "880601-2455116");
        User user4 = new User((long) 4, "be12", "123456", "베지터", "910411-1656116");
        User user5 = new User((long) 5, "son12", "123456", "손오공", "820326-2715702");

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);

        return userList;
    }
}
