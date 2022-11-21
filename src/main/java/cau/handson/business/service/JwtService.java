package cau.handson.business.service;

import cau.handson.business.constant.Code;
import cau.handson.business.domain.Jwt;
import cau.handson.business.domain.User;
import cau.handson.business.dto.JwtDto;
import cau.handson.business.jwt.TokenProvider;
import cau.handson.business.repository.JwtRepository;
import cau.handson.business.repository.UserRepository;
import cau.handson.business.util.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class JwtService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final JwtRepository jwtRepository;

    public JwtDto reissueJwt(JwtDto jwtDto) {
        if (!tokenProvider.validateToken(jwtDto.getRefreshToken())) {
            throw new GeneralException(Code.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(jwtDto.getAccessToken());
        String userId = authentication.getName();

        Jwt jwt = jwtRepository.findOneByUserId(userId)
            .orElseThrow(() -> new GeneralException(Code.REFRESH_TOKEN_NOT_FOUND));
        if (!jwt.getRefreshToken().equals(jwtDto.getRefreshToken())) {
            throw new GeneralException(Code.INVALID_REFRESH_TOKEN);
        }

        jwtRepository.delete(jwt);

        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND));
        JwtDto newJwtDto = tokenProvider.generateJwt(user);
        Jwt newJwt = Jwt.builder().user(user).refreshToken(newJwtDto.getRefreshToken()).build();
        jwtRepository.save(newJwt);

        return newJwtDto;
    }

    public JwtDto issueJwt(User user) {
        jwtRepository.findOneByUserId(user.getId()).ifPresent(jwtRepository::delete);
        JwtDto jwtDto = tokenProvider.generateJwt(user);
        Jwt jwt = Jwt.builder().user(user).refreshToken(jwtDto.getRefreshToken()).build();
        jwtRepository.save(jwt);
        return jwtDto;
    }

}