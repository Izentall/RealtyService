package ru.gr5140904_30201.kichu.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.gr5140904_30201.kichu.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean existsByEmail(String email) {
        return namedParameterJdbcTemplate.query("""
                select count(*) as cnt
                from users
                where email = :email
                """,
                new MapSqlParameterSource("email", email),
                (rs, rn) -> rs.getLong("cnt")
        ).get(0) != 0;
    }

    public void save(User user) {
        namedParameterJdbcTemplate.update("""
                insert into users (email, password, name, user_role)
                values (:email, :password, :name, :user_role::user_role)
                """,
                new MapSqlParameterSource()
                        .addValue("email", user.getEmail())
                        .addValue("password", user.getPassword())
                        .addValue("name", user.getName())
                        .addValue("user_role", user.getUserRole())
        );
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(namedParameterJdbcTemplate.query("""
                select *
                from users
                where email = :email
                """,
                new MapSqlParameterSource("email", email),
                UserDao::map
        ).get(0));
    }

    private static User map(ResultSet rs, int rn) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .userRole(rs.getString("user_role"))
                .build();
    }
}
