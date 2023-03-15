package sk.janvanca.todolist.services.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import sk.janvanca.todolist.domain.User;
import sk.janvanca.todolist.mapper.UserRowMapper;
import sk.janvanca.todolist.services.api.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    public UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        final String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User get(Integer id) {
        final String sql = "SELECT * FROM user WHERE user.id = " + id;

        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public Integer add(User user) {
        final String sql = "INSERT INTO user (name, surname, nickname, email, age, password) values (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getSurname());
                preparedStatement.setString(3, user.getNickname());
                preparedStatement.setString(4, user.getEmail());

                if (user.getAge() != null) {
                    preparedStatement.setInt(5, user.getAge());
                } else {
                    preparedStatement.setNull(5, Types.INTEGER);
                }
                preparedStatement.setString(6, user.getPassword());
                return preparedStatement;
            }
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().intValue();
        } else {
            return null;
        }
    }

    @Override
    public void delete(Integer id) {
        final String sql = "DELETE FROM user WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Integer id, User user) {
        final String sql = "UPDATE user SET name = ?, surname = ?, nickname = ?, email = ?, age = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getSurname(), user.getNickname(), user.getEmail(), user.getAge(), user.getPassword(), id);
    }
}
