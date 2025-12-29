package contest_site.contest_site.repo;

import contest_site.contest_site.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}