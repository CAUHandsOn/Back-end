package cau.handson.business.dto;


import cau.handson.business.domain.RoomUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomUserDto {

    private UserDto user;
    private RoomDto room;
    private LocalDateTime getIn;

    public RoomUserDto() {
    }

    public static RoomUserDto baseResponse(RoomUser roomUser) {
        return RoomUserDto.builder()
            .room(RoomDto.baseResponse(roomUser.getRoom()))
            .user(UserDto.baseResponse(roomUser.getUser()))
            .getIn(roomUser.getCreatedAt())
            .build();
    }

    public static RoomUserDto userResponse(RoomUser roomUser) {
        return RoomUserDto.builder()
            .user(UserDto.baseResponse(roomUser.getUser()))
            .getIn(roomUser.getCreatedAt())
            .build();
    }

}

