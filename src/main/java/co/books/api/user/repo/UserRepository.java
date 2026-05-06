package co.books.api.user.repo;

import co.books.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 정보 레포지토리.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /** 이메일로 회원 조회 (로그인, 중복 체크에 활용) */
    Optional<UserEntity> findByEmail(String email);

    /** 이메일 존재 여부 확인 */
    boolean existsByEmail(String email);
}
