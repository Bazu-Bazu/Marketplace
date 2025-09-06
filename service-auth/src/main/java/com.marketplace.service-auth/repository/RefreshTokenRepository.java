package marketplace.User.Auth.Service.repository;

import marketplace.User.Auth.Service.entity.RefreshToken;
import marketplace.User.Auth.Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    void deleteAllByUser(User user);

}
