package cau.handson.business.repository;

import cau.handson.business.domain.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    Optional<Room> findById(String id);

    List<Room> findAll();

    void deleteById(String id);
}
