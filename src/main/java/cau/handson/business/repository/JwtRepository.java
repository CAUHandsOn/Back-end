package cau.handson.business.repository;

import cau.handson.business.domain.Jwt;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {

    Optional<Jwt> findOneByUserId(String userId);

    void deleteByUserId(Long userId);
}
