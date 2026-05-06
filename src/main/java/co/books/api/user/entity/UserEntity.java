package co.books.api.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * 회원 정보 엔티티.
 * users 테이블과 매핑된다.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(name = "user_id", length = 100)
    private String userId;

    /** 이메일 (로그인 ID) */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** 암호화된 비밀번호 (bcrypt 등) */
    @Column(nullable = false, length = 255)
    private String passwd;

    /** 회원 이름 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 전화번호 */
    @Column(length = 20)
    private String phone;

    /** 성별 (남자/여자) */
    @Column(length = 20)
    private String gender;

    /** 생년월일 */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /** 우편번호 */
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /** 기본 주소 (도로명/지번) */
    @Column(length = 255)
    private String address;

    /** 상세 주소 (동/호수 등) */
    @Column(name = "address_detail", length = 255)
    private String addressDetail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}