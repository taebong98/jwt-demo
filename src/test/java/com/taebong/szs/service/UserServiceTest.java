package com.taebong.szs.service;

import com.taebong.szs.common.exception.DataNotFoundException;
import com.taebong.szs.common.exception.ForbiddenException;
import com.taebong.szs.common.exception.LoginException;
import com.taebong.szs.common.jwt.JwtTokenProvider;
import com.taebong.szs.common.util.CryptUtils;
import com.taebong.szs.controller.dto.LoginDto;
import com.taebong.szs.controller.dto.scrapapidto.*;
import com.taebong.szs.domain.UserService;
import com.taebong.szs.domain.user.repository.UserRepository;
import com.taebong.szs.domain.user.vo.AllowedUsers;
import com.taebong.szs.domain.user.vo.Deduction;
import com.taebong.szs.domain.user.vo.DeductionCategory;
import com.taebong.szs.domain.user.vo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;

import java.util.*;

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
    String scrapEndPoint;

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

        scrapEndPoint = "https://codetest.3o3.co.kr/v2/scrap";
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
    public void 로그인_회원_정보가_일치한다면_토큰을_발급한다() throws Exception {
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

    @Test
    public void 토큰에서_사용자_정보_조회() throws Exception {
        // given
        String mockToken = "mockedToken";
        Claims mockedClaims = mock(Claims.class);
        when(jwtTokenProvider.getClaims(mockToken)).thenReturn(mockedClaims);
        when(mockedClaims.get("name")).thenReturn("MockedName");
        when(mockedClaims.get("userId")).thenReturn("MockedUserId");
        when(mockedClaims.get("password")).thenReturn("MockedEncryptedPassword");
        when(mockedClaims.get("regNo")).thenReturn("MockedEncryptedRegNo");
        when(cryptUtils.decrypt("MockedEncryptedPassword")).thenReturn("DecryptedPassword");
        when(cryptUtils.decrypt("MockedEncryptedRegNo")).thenReturn("DecryptedRegNo");

        // when
        User resultUser = userService.getUserInJwtToken(mockToken);

        // then
        verify(jwtTokenProvider).getClaims(mockToken);
        verify(cryptUtils).decrypt("MockedEncryptedPassword");
        verify(cryptUtils).decrypt("MockedEncryptedRegNo");

        assertThat("MockedName").isEqualTo(resultUser.getName());
        assertThat("MockedUserId").isEqualTo(resultUser.getUserId());
        assertThat("DecryptedPassword").isEqualTo(resultUser.getPassword());
        assertThat("DecryptedRegNo").isEqualTo(resultUser.getRegNo());
    }

    @Test
    public void 만료된_토큰_예외_발생() throws Exception {
        // given
        String invalidToken = "invalidToken";
        when(jwtTokenProvider.getClaims(invalidToken)).thenThrow(ExpiredJwtException.class);

        // when & then
        assertThrows(ExpiredJwtException.class, () -> userService.getUserInJwtToken(invalidToken));
    }

    @Test
    public void 잘못된_토큰_시그니처() throws Exception {
        // given
        String invalidToken = "invalidToken";
        when(jwtTokenProvider.getClaims(invalidToken)).thenThrow(MalformedJwtException.class);

        assertThrows(MalformedJwtException.class, () -> userService.getUserInJwtToken(invalidToken));
    }

    public ResponseEntity<ScrapResponseDto> hong12ScrapRequestDtoFixture() {
        List<SalaryResponseDto> salaryResponseDtoList = new ArrayList<>();
        salaryResponseDtoList.add(new SalaryResponseDto(
                "급여",
                "60,000,000",
                "2020.10.02",
                "(주)활빈당",
                "홍길동",
                "2020.11.02",
                "2021.11.02",
                "860824-1655068",
                "근로소득(연간)",
                "012-34-56789"
        ));

        List<DeductionResponseDto> deductionResponseDtoList = new ArrayList<>();
        deductionResponseDtoList.add(new DeductionResponseDto("100,000", "보험료", null));
        deductionResponseDtoList.add(new DeductionResponseDto("200,000", "교육비", null));
        deductionResponseDtoList.add(new DeductionResponseDto("150,000", "기부금", null));
        deductionResponseDtoList.add(new DeductionResponseDto("4,400,000", "의료비", null));
        deductionResponseDtoList.add(new DeductionResponseDto(null, "퇴직연금", "6,000,000"));

        JsonListResponseDto jsonListResponseDto = new JsonListResponseDto(salaryResponseDtoList, "3000000", deductionResponseDtoList);

        ScrapDataResponseDto scrapDataResponseDto = new ScrapDataResponseDto(
                jsonListResponseDto,
                "2021112501",
                "",
                "삼쩜삼",
                "test01",
                "jobis-codedtest",
                "2022-08-16T06:27:35.160789",
                "2022-08-16T06:27:35.160851"
        );

        return ResponseEntity.ok(new ScrapResponseDto("success", scrapDataResponseDto, null));
    }
    
    @Test
    public void 스크랩_정보를_이용해_결정새액과_퇴직연금공제금을_계산한다_홍길동() throws Exception {
        // given
        List<Deduction> deductionList = hongGilDongScrapDataFixture();

        User hongGilDong = User.builder()
                .id(1L)
                .userId("hong12")
                .taxAmount("3,000,000")
                .deductionList(deductionList)
                .build();

        double insurancePaid = 100000;
        double educationPaid = 200000;
        double donationPaid = 150000;
        double medicalPaid = 4400000;
        double totalSalary = 6000000;

        // when
        
        // then
    }


    private List<Deduction> hongGilDongScrapDataFixture() {
        List<Deduction> deductionList = new ArrayList<>();
        Deduction hongDeductionInsurance = Deduction.builder()
                .deductionAmount("100,000")
                .deductionCategory(DeductionCategory.INSURANCE)
                .build();

        Deduction hongDeductionEducation = Deduction.builder()
                .deductionAmount("200,000")
                .deductionCategory(DeductionCategory.EDUCATION)
                .build();

        Deduction hongDeductionDonation = Deduction.builder()
                .deductionAmount("150,000")
                .deductionCategory(DeductionCategory.DONATION)
                .build();

        Deduction hongDeductionMedical = Deduction.builder()
                .deductionAmount("4,400,000")
                .deductionCategory(DeductionCategory.MEDICAL)
                .build();

        Deduction hongDeductionRetire = Deduction.builder()
                .totalPayment("6,000,000")
                .deductionCategory(DeductionCategory.RETIREMENT_PENSION)
                .build();

        deductionList.add(hongDeductionInsurance);
        deductionList.add(hongDeductionEducation);
        deductionList.add(hongDeductionDonation);
        deductionList.add(hongDeductionMedical);
        deductionList.add(hongDeductionRetire);
        return deductionList;
    }

    private List<Deduction> maZingGaScrapDataFixture() {
        List<Deduction> deductionList = new ArrayList<>();
        deductionList.add(Deduction.builder().deductionCategory(DeductionCategory.INSURANCE).deductionAmount("200,000").build());
        deductionList.add(Deduction.builder().deductionCategory(DeductionCategory.EDUCATION).deductionAmount("200,000").build());
        deductionList.add(Deduction.builder().deductionCategory(DeductionCategory.DONATION).deductionAmount("200,000").build());
        deductionList.add(Deduction.builder().deductionCategory(DeductionCategory.MEDICAL).deductionAmount("5,000,000").build());
        deductionList.add(Deduction.builder().deductionCategory(DeductionCategory.RETIREMENT_PENSION).totalPayment("7,233,333.333").build());
        return deductionList;
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
