package com.example.demo.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JwtUserRepository extends JpaRepository<JwtUser, Long> {
	Optional<JwtUser> findJwtUserByUsername(String username);

	Optional<JwtUser> findJwtUserByEmail(String email);
	@Query("SELECT u FROM JwtUser u WHERE MONTH(u.birthday) = :month AND DAY(u.birthday) = :day")
	List<JwtUser> findByBirthdayDayAndMonth(@Param("day") int day, @Param("month") int month);

	List<JwtUser> findJwtUserByPositionIsNot(Position position);

	@Query(value = "select *,0 as clazz_ from jwt_user u where u.birthday IS NOT NULL and (u.position = 'Teacher' or u.position = 'Student')",
//	@Query(value = "select *, 0 as clazz_ from jwt_user u where u.birthday IS NOT NULL",
			nativeQuery = true)
	List<JwtUser> findAllByPositionOrPosition (Position position, Position position2);

	@Query(value = "SELECT u FROM JwtUser u WHERE LENGTH(u.password) < 50")
	List<JwtUser> findUsersWithShortPassword();
}
