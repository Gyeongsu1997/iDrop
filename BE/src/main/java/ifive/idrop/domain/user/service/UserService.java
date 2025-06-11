package ifive.idrop.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifive.idrop.domain.auth.Auth;
import ifive.idrop.domain.auth.repository.AuthRepository;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.domain.auth.dto.LoginRequest;
import ifive.idrop.domain.driver.Driver;
import ifive.idrop.domain.user.User;
import ifive.idrop.domain.user.dto.SignUpRequest;
import ifive.idrop.domain.user.dto.NameResponse;
import ifive.idrop.common.enums.Role;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.domain.auth.AuthenticateUser;
import ifive.idrop.domain.auth.filter.VerifyUserFilter;
import ifive.idrop.domain.auth.dto.Jwt;
import ifive.idrop.domain.auth.utils.JwtProvider;
import ifive.idrop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Transactional
    public BaseResponse<String> signUp(SignUpRequest signUpRequest){
        checkDuplicateLoginId(signUpRequest.getLoginId());
        User user = signUpRequest.toEntity();
        userRepository.save(user);
        if (user instanceof Driver)
            return BaseResponse.of("성공적으로 회원가입 되었습니다.", "기사");
        else
            return BaseResponse.of("성공적으로 회원가입 되었습니다.", "부모");
    }

    private void checkDuplicateLoginId(String loginId) {
        Optional<User> optional = userRepository.findByLoginId(loginId);
        if (optional.isPresent())
            throw new BusinessException(ErrorCode.DUPLICATE_USERID);
    }

    public Role verifyUser(LoginRequest loginRequest){
        Optional<User> optional = userRepository.findByLoginId(loginRequest.getUserId());
        User user = optional.orElseThrow(() -> new BusinessException(ErrorCode.USERID_NOT_EXIST));
        if (!user.verifyUser(loginRequest))
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCHED);
        if (user instanceof Driver) {
            return Role.DRIVER;
        } else {
            return Role.PARENT;
        }
    }

    @Transactional
    public void updateRefreshToken(String loginId, String refreshToken){
        Optional<User> optional = userRepository.findByLoginId(loginId);
        if (optional.isEmpty())
            return;
        User user = optional.get();
        user.updateRefreshToken(refreshToken);
    }


    @Transactional
    public Jwt createNewJwtFromRefreshToken(String refreshToken) {
        try{
            // 유효한 토큰 인지 검증
            jwtProvider.getClaims(refreshToken);
            Optional<Auth> optional = authRepository.findByRefreshToken(refreshToken);
            if (optional.isEmpty())
                return null;
            User user = userRepository.findById(optional.get().getUserId()).get(); // todo: 예외처리

            HashMap<String, Object> claims = new HashMap<>();
            AuthenticateUser authenticateUser;
            if (user instanceof Driver)
                authenticateUser = new AuthenticateUser(user.getLoginId(), Role.DRIVER);
            else
                authenticateUser = new AuthenticateUser(user.getLoginId(), Role.PARENT);
            String authenticateUserJson = objectMapper.writeValueAsString(authenticateUser);
            claims.put(VerifyUserFilter.AUTHENTICATE_USER, authenticateUserJson);
            Jwt jwt = jwtProvider.createJwt(claims);
            updateRefreshToken(user.getLoginId(), jwt.getRefreshToken());
            return jwt;
        } catch (Exception e){
            return null;
        }
    }

    public NameResponse getName(User user) {
        User foundUser = userRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return new NameResponse(foundUser);
    }

    @Transactional
    public BaseResponse<String> updateFCMToken(String userId, String fcmToken) {
        User foundUser = userRepository.findByLoginId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        foundUser.updateFcmToken(fcmToken);
        return BaseResponse.success();
    }
}
