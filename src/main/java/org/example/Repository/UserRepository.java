package org.example.Repository;

import org.example.Entity.UserBalanceEntity;
import org.example.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<UserEntity> userRowMapper() {
        return (rs, rowNum) -> {
            UserEntity user = new UserEntity();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setFullname(rs.getString("fullname"));
            user.setProfilePic(rs.getString("profile_pic"));
            user.setCreatedAt(rs.getDate("created_at"));
            return user;
        };
    }

    public UserEntity save(UserEntity user) {
        String sql = "INSERT INTO users (username, password, email, fullname, created_at) VALUES (?, ?, ?, ?, now())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getFullname());
                return ps;
            }
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;

    }

    public UserBalanceEntity saveBalance(UserBalanceEntity balance) {
        String sql = "INSERT INTO user_balance (user_id, balance, updated_at) VALUES (?, ?, now())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setInt(1, balance.getUser().getId());
                ps.setBigDecimal(2, balance.getBalance());
                return ps;
            }
        }, keyHolder);

        balance.setId(keyHolder.getKey().intValue());

        return balance;

    }

    public UserEntity update(UserEntity user) {
        String sql = "UPDATE users SET fullname = ? WHERE username = ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getFullname());
                ps.setString(2, user.getUsername());
                return ps;
            }
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;

    }

    public UserEntity updatePicture(UserEntity user) {
        String sql = "UPDATE users SET profile_pic = ? WHERE username = ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getProfilePic());
                ps.setString(2, user.getUsername());
                return ps;
            }
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;

    }

    public UserEntity findByUsername(String username) {
        try {
            String sql = "SELECT * FROM USERS WHERE USERNAME = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, userRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserEntity findById(Integer id) {
        try {
            String sql = "SELECT * FROM USERS WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, userRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserEntity findByEmail(String username) {
        try {
            String sql = "SELECT * FROM USERS WHERE EMAIL = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, userRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


}
