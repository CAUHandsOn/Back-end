package cau.handson.business.controller;

import cau.handson.business.dto.DataResponseDto;
import cau.handson.business.dto.UserDto;
import cau.handson.business.service.JwtService;
import cau.handson.business.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping(path = "/sign-up")
    public DataResponseDto<Object> register(@RequestBody UserDto userDto) {
        return DataResponseDto.of(userService.register(userDto));
    }

    @PostMapping(path = "/sign-in")
    public DataResponseDto<Object> signIn(@RequestBody UserDto userDto) {
        return DataResponseDto.of(userService.signIn(userDto));
    }
}
