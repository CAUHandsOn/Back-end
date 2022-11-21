package cau.handson.business.service;


import cau.handson.business.constant.Code;
import cau.handson.business.domain.User;
import cau.handson.business.dto.JwtDto;
import cau.handson.business.dto.UserDto;
import cau.handson.business.repository.UserRepository;
import cau.handson.business.util.GeneralException;
import cau.handson.business.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public JwtDto register(UserDto dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        return jwtService.issueJwt(userRepository.save(dto.toEntity()));
    }

    public JwtDto signIn(UserDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new GeneralException(Code.UNAUTHORIZED, "ID/PW Wrong"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new GeneralException(Code.UNAUTHORIZED, "ID/PW Wrong");
        }
        return jwtService.issueJwt(user);
    }

    public UserDto getMe() {
        return get(SecurityUtil.getUserId());
    }

    public UserDto get(String id) {
        return UserDto.baseResponse(userRepository.findById(id)
            .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND)));
    }

    public UserDto updateMe(UserDto dto) {
        return update(SecurityUtil.getUserId(), dto);
    }

    public UserDto update(String id, UserDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND));
        userRepository.save(dto.updateEntity(user));
        return UserDto.baseResponse(user);
    }

    public Boolean deleteMe() {
        return delete(SecurityUtil.getUserId());
    }

    public Boolean delete(String id) {
        userRepository.deleteById(id);
        return true;
    }
}
