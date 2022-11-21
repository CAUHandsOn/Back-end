package cau.handson.business.dto;


import cau.handson.business.domain.Room;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
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
public class RoomDto {

    private String id;
    private String name;
    private List<RoomUserDto> roomMembers;


    public RoomDto() {
    }

    public static RoomDto baseResponse(Room room) {
        return RoomDto.builder()
            .id(room.getId())
            .name(room.getName())
            .build();
    }

    public Room toEntity() {
        return Room.builder()
            .id(id)
            .name(name)
            .build();
    }

    public Room updateEntity(Room room) {
        Optional.ofNullable(id).ifPresent(room::setId);
        Optional.ofNullable(name).ifPresent(room::setName);
        return room;
    }
}

