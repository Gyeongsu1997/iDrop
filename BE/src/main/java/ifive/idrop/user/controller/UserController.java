package ifive.idrop.user.controller;

import ifive.idrop.auth.resolver.Login;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.user.dto.NameResponse;
import ifive.idrop.user.dto.SignUpRequest;
import ifive.idrop.user.domain.User;
import ifive.idrop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
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
