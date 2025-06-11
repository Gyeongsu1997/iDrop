package ifive.idrop.domain.user.controller;

import ifive.idrop.domain.auth.resolver.Login;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.domain.user.User;
import ifive.idrop.domain.user.dto.SignUpRequest;
import ifive.idrop.domain.user.service.UserService;
import ifive.idrop.domain.user.dto.NameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public BaseResponse<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        return userService.signUp(signUpRequest);
    }

    @GetMapping("/name")
    public NameResponse getName(@Login User user) {
        return userService.getName(user);
    }
}
