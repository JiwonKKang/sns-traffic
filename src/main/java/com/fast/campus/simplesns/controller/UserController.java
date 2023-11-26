package com.fast.campus.simplesns.controller;


import com.fast.campus.simplesns.controller.request.UserJoinRequest;
import com.fast.campus.simplesns.controller.request.UserLoginRequest;
import com.fast.campus.simplesns.controller.response.AlarmResponse;
import com.fast.campus.simplesns.controller.response.Response;
import com.fast.campus.simplesns.controller.response.UserJoinResponse;
import com.fast.campus.simplesns.controller.response.UserLoginResponse;
import com.fast.campus.simplesns.exception.ErrorCode;
import com.fast.campus.simplesns.exception.SimpleSnsApplicationException;
import com.fast.campus.simplesns.model.User;
import com.fast.campus.simplesns.service.UserService;
import com.fast.campus.simplesns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        return Response.success(UserJoinResponse.fromUser(userService.join(request.getName(), request.getPassword())));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/me")
    public Response<UserJoinResponse> me(Authentication authentication) {
        return Response.success(UserJoinResponse.fromUser(userService.loadUserByUsername(authentication.getName())));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Authentication authentication, Pageable pageable) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)
                .orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR));
        return Response.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }
}
