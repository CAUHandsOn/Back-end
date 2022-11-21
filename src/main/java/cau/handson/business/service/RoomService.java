package cau.handson.business.service;


import cau.handson.business.constant.Code;
import cau.handson.business.domain.Room;
import cau.handson.business.dto.RoomDto;
import cau.handson.business.repository.RoomRepository;
import cau.handson.business.util.GeneralException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomUserService roomUserService;

    public RoomDto insert(RoomDto dto) {
        return RoomDto.baseResponse(roomRepository.save(dto.toEntity()));
    }

    public RoomDto get(String id) {
        RoomDto dto = RoomDto.baseResponse(roomRepository.findById(id)
            .orElseThrow(() -> new GeneralException(Code.NOT_FOUND)));
        dto.setRoomMembers(roomUserService.getByRoomId(id));
        return dto;
    }

    public List<RoomDto> getAll() {
        return roomRepository.findAll().stream().map(RoomDto::baseResponse).collect(Collectors.toList());
    }

    public RoomDto update(String id, RoomDto dto) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new GeneralException(Code.NOT_FOUND));
        roomRepository.save(dto.updateEntity(room));
        return RoomDto.baseResponse(room);
    }

    public Boolean delete(String id) {
        roomRepository.deleteById(id);
        return true;
    }
}
