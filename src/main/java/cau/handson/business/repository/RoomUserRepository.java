package cau.handson.business.repository;

import cau.handson.business.domain.RoomUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomUserRepository extends JpaRepository<RoomUser, String> {

    List<RoomUser> findByRoomId(String roomId);

    void deleteByUserId(String user);
}
