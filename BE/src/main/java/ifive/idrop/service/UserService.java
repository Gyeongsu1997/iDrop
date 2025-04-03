package ifive.idrop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.auth.dto.LoginRequest;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.dto.response.NameResponse;
import ifive.idrop.dto.request.SignUpRequest;
import ifive.idrop.user.domain.User;
import ifive.idrop.entity.enums.Role;
import ifive.idrop.common.exception.CommonException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.auth.domain.AuthenticateUser;
import ifive.idrop.auth.filter.VerifyUserFilter;
import ifive.idrop.auth.dto.Jwt;
import ifive.idrop.auth.utils.JwtProvider;
import ifive.idrop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Transactional
    public BaseResponse<String> signUp(SignUpRequest signUpRequest){
        checkDuplicateUserId(signUpRequest.getUserId());
        User user = signUpRequest.toEntity();
        userRepository.save(user);
        if (user instanceof Driver)
            return BaseResponse.of("성공적으로 회원가입 되었습니다.", "기사");
        else
            return BaseResponse.of("성공적으로 회원가입 되었습니다.", "부모");
    }

    public void checkDuplicateUserId(String userId) {
        Optional<User> optional = userRepository.findByLoginId(userId);
        if (optional.isPresent())
            throw new CommonException(ErrorCode.DUPLICATE_USERID);
    }

    public Role verifyUser(LoginRequest loginRequest){
        Optional<User> optional = userRepository.findByLoginId(loginRequest.getUserId());
        User user = optional.orElseThrow(() -> new CommonException(ErrorCode.USERID_NOT_EXIST));
        if (!user.verifyUser(loginRequest))
            throw new CommonException(ErrorCode.PASSWORD_NOT_MATCHED);
        if (user instanceof Driver)
            return Role.DRIVER;
        else
            return Role.PARENT;
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
            Optional<User> optional = userRepository.findByRefreshToken(refreshToken);
            if (optional.isEmpty())
                return null;
            User user = optional.get();

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

    @Transactional(readOnly = true)
    public NameResponse getName(User user) {
        User foundUser = userRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));
        return new NameResponse(foundUser);
    }

    @Transactional
    public BaseResponse<String> updateFCMToken(String userId, String fcmToken) {
        User foundUser = userRepository.findByLoginId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));
        foundUser.updateFcmToken(fcmToken);
        return BaseResponse.success();
    }
}
