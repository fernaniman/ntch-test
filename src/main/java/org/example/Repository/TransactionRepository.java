package org.example.Repository;

import org.example.Dto.TransactionResponseDto;
import org.example.Entity.ServiceEntity;
import org.example.Entity.UserBalanceEntity;
import org.example.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    public UserBalanceEntity findBalanceByUserId(Integer userId) {
        try {
            String sql = "SELECT * FROM USER_BALANCE WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, (rs, rowNum) -> {
                UserBalanceEntity balance = new UserBalanceEntity();
                balance.setId(rs.getInt("id"));
                balance.setBalance(rs.getBigDecimal("balance"));
                UserEntity user = userRepository.findById(rs.getInt("user_id"));
                balance.setUser(user);
                balance.setUpdatedAt(rs.getDate("updated_at"));
                return balance;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserBalanceEntity updateBalance(BigDecimal amount, Integer userId, Boolean isTopup) {
        UserBalanceEntity currentBalance = findBalanceByUserId(userId);
        BigDecimal totalAmount;
        if(isTopup) {
            totalAmount = currentBalance.getBalance().add(amount);
        } else {
            totalAmount = currentBalance.getBalance().subtract(amount);
        }

        String sql = "UPDATE user_balance SET balance = ?, updated_at = now() WHERE user_id = ?";

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{});
                ps.setBigDecimal(1, totalAmount);
                ps.setInt(2, userId);

                return ps;
            }
        });

        UserBalanceEntity balance = findBalanceByUserId(userId);

        return balance;

    }

    public TransactionResponseDto saveTransaction(ServiceEntity service, Integer userId, String invoiceNumber) {
        String sql = "INSERT INTO transaction_history (user_id, service_id, transaction_type, invoice_number, total_amount, created_at) VALUES (?, ?, ?, ?, ?, now())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setInt(1, userId);
                ps.setInt(2, service.getId());
                ps.setString(3, "PAYMENT");
                ps.setString(4, invoiceNumber);
                ps.setBigDecimal(5, service.getTarif());
                return ps;
            }
        }, keyHolder);

        int newTransactionId = keyHolder.getKey().intValue();

        String selectSql = "select th.invoice_number, s.code, s.\"name\", th.transaction_type, th.total_amount, th.created_at from transaction_history th join services s on th.service_id = s.id where th.id = ?";

        return jdbcTemplate.queryForObject(selectSql, new Object[]{newTransactionId}, (rs, rowNum) -> {
            TransactionResponseDto response =  new TransactionResponseDto();
            response.setInvoice_number(rs.getString("invoice_number"));
            response.setService_code(rs.getString("code"));
            response.setService_name(rs.getString("name"));
            response.setTransaction_type(rs.getString("transaction_type"));
            response.setTotal_amount(rs.getBigDecimal("total_amount"));
            response.setTotal_amount(rs.getBigDecimal("total_amount"));
            response.setCreated_at(rs.getDate("created_at"));
            return response;
        });

    }

    public List<TransactionResponseDto> getTransactionHistory(Integer limit, Integer offset) {
        String sql = "SELECT COUNT(*) FROM transaction_history;";
        Integer sizeAll = jdbcTemplate.queryForObject(sql, new Object[]{}, Integer.class);

        String selectSql = "SELECT th.invoice_number, s.code, s.\"name\", th.transaction_type, th.total_amount, th.created_at " +
                "FROM transaction_history th " +
                "JOIN services s ON th.service_id = s.id " +
                "ORDER BY th.created_at DESC LIMIT ? OFFSET ?";

        return jdbcTemplate.query(selectSql, new Object[]{limit != null ? limit : sizeAll, offset != null ? offset : 0}, (rs, rowNum) -> {
            TransactionResponseDto response = new TransactionResponseDto();
            response.setInvoice_number(rs.getString("invoice_number"));
            response.setService_code(rs.getString("code"));
            response.setService_name(rs.getString("name"));
            response.setTransaction_type(rs.getString("transaction_type"));
            response.setTotal_amount(rs.getBigDecimal("total_amount"));
            response.setCreated_at(rs.getDate("created_at"));
            return response;
        });
    }


    public String getLastInvoiceNumber() {
        try {
            String sql = "SELECT th.invoice_number FROM transaction_history th WHERE DATE(th.created_at) = DATE(now()) ORDER BY th.created_at DESC LIMIT 1";
            String invoiceNumber = jdbcTemplate.queryForObject(sql, new Object[]{}, String.class);
            return invoiceNumber;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
