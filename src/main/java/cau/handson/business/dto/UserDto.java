package cau.handson.business.dto;


import cau.handson.business.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String id;
    private String email;
    private String name;
    private String role;
    private String password;

    public UserDto() {
    }

    public static UserDto baseResponse(User user) {
        return UserDto.builder()
            .email(user.getEmail())
            .id(user.getId())
            .name(user.getName())
            .role(user.getRole())
            .build();
    }

    public User toEntity() {
        return User.builder()
            .id(id)
            .name(name)
            .email(email)
            .role(role)
            .password(password)
            .build();
    }

    public User updateEntity(User user) {
        Optional.ofNullable(name).ifPresent(user::setName);
        Optional.ofNullable(role).ifPresent(user::setRole);
        Optional.ofNullable(id).ifPresent(user::setId);
        Optional.ofNullable(email).ifPresent(user::setId);

        return user;
    }
}

