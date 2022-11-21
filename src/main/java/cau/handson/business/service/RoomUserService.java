package cau.handson.business.service;


import cau.handson.business.constant.Code;
import cau.handson.business.domain.Room;
import cau.handson.business.domain.RoomUser;
import cau.handson.business.domain.User;
import cau.handson.business.dto.RoomUserDto;
import cau.handson.business.repository.RoomRepository;
import cau.handson.business.repository.RoomUserRepository;
import cau.handson.business.repository.UserRepository;
import cau.handson.business.util.GeneralException;
import cau.handson.business.util.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomUserService {

    private final RoomUserRepository roomMemberRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public RoomUserDto insertMe(String id) {
        return insert(SecurityUtil.getUserId(), id);
    }

    public RoomUserDto insert(String userId, String roomId) {
        delete(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new GeneralException(Code.NOT_FOUND));
        RoomUser roomUser = RoomUser.builder().user(user).room(room).build();
        return RoomUserDto.baseResponse(roomMemberRepository.save(roomUser));
    }

    public Boolean deleteMe() {
        return delete(SecurityUtil.getUserId());
    }

    public Boolean delete(String userId) {
        roomMemberRepository.deleteByUserId(userId);
        return true;
    }

    public List<RoomUserDto> getByRoomId(String id) {
        return roomMemberRepository.findByRoomId(id).stream().map(RoomUserDto::userResponse).toList();
    }
}
