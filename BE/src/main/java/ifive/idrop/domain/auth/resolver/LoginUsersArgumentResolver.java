package ifive.idrop.domain.auth.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ifive.idrop.domain.auth.filter.VerifyUserFilter;
import ifive.idrop.domain.auth.utils.JwtProvider;
import ifive.idrop.domain.user.User;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.domain.auth.AuthenticateUser;
import ifive.idrop.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@RequiredArgsConstructor
public class LoginUsersArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasUsersType = User.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasUsersType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = getToken(httpServletRequest);
        AuthenticateUser authenticateUser = getAuthenticateUser(token);
        Optional<User> optional = userRepository.findByLoginId(authenticateUser.getUserId());
        User user = optional.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return user;
    }

    private String getToken(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        return authorization.substring(7);
    }

    private AuthenticateUser getAuthenticateUser(String token) throws JsonProcessingException {
        Claims claims = jwtProvider.getClaims(token);
        String authenticateUserJson = claims.get(VerifyUserFilter.AUTHENTICATE_USER, String.class);
        return objectMapper.readValue(authenticateUserJson, AuthenticateUser.class);
    }
}
