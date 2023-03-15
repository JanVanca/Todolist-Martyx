package sk.janvanca.todolist.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import sk.janvanca.todolist.domain.Task;
import sk.janvanca.todolist.mapper.TaskRowMapper;
import sk.janvanca.todolist.services.api.TaskService;

import java.sql.*;
import java.time.Instant;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {


    private final JdbcTemplate jdbcTemplate;
    private final TaskRowMapper taskRowMapper = new TaskRowMapper();

    public TaskServiceImpl (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Task> getTasks() {
        final String sql = "SELECT * FROM task";
        return jdbcTemplate.query(sql, taskRowMapper);
    }

    @Override
    public Task get(Integer id) {
        final String sql = "SELECT * FROM task WHERE task.id = " + id;
        try {
            return jdbcTemplate.queryForObject(sql, taskRowMapper);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public List<Task> getTaskByUserId(Integer userId) {
        final String sql = "SELECT * FROM task WHERE task.userId = " + userId;
        try {
            return jdbcTemplate.query(sql, taskRowMapper);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public Integer add(Task task) {
        final String sql = "INSERT INTO task(userId, name, status, category, description, createdAt) values (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, task.getUserId());
                preparedStatement.setString(2, task.getName());
                preparedStatement.setInt(3, task.getStatus());
                preparedStatement.setString(4, task.getCategory());
                preparedStatement.setString(5, task.getDescription());
                preparedStatement.setTimestamp(6, Timestamp.from(Instant.now()));
                return preparedStatement;
            }
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().intValue();
        }
        else {
            return null;
        }
    }

    @Override
    public void delete(Integer id) {
        final String sql = "DELETE FROM task WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Integer id, Task task) {
        final String sql = "UPDATE task SET name = ?, status = ?, category = ?,description = ? WHERE id = ?";
        jdbcTemplate.update(sql, task.getName(), task.getStatus(), task.getCategory(), task.getDescription(), id);
    }
}
