package org.example.Repository;

import org.example.Entity.BannerEntity;
import org.example.Entity.ServiceEntity;
import org.example.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InfoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BannerEntity> findAllBanner() {
        String sql = "SELECT * FROM BANNER";

        return jdbcTemplate.query(sql, new RowMapper<BannerEntity>() {
            @Override
            public BannerEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                BannerEntity banner = new BannerEntity();
                banner.setId(rs.getInt("id"));
                banner.setName(rs.getString("name"));
                banner.setImage(rs.getString("image"));
                banner.setDescription(rs.getString("description"));
                return banner;
            }
        });
    }

    public List<ServiceEntity> findAllService() {
        String sql = "SELECT * FROM SERVICES";
        return jdbcTemplate.query(sql, new RowMapper<ServiceEntity>() {
            @Override
            public ServiceEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                ServiceEntity service = new ServiceEntity();
                service.setId(rs.getInt("id"));
                service.setCode(rs.getString("code"));
                service.setName(rs.getString("name"));
                service.setIcon(rs.getString("icon"));
                service.setTarif(rs.getBigDecimal("tarif"));
                return service;
            }
        });
    }

    public ServiceEntity findServiceByName(String service) {
        String sql = "SELECT id, code, name, icon, tarif FROM services WHERE UPPER(name) = UPPER(?) ORDER BY created_at DESC LIMIT 1";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{service}, new RowMapper<ServiceEntity>() {
                @Override
                public ServiceEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ServiceEntity serviceEntity = new ServiceEntity();
                    serviceEntity.setId(rs.getInt("id"));
                    serviceEntity.setCode(rs.getString("code"));
                    serviceEntity.setName(rs.getString("name"));
                    serviceEntity.setIcon(rs.getString("icon"));
                    serviceEntity.setTarif(rs.getBigDecimal("tarif"));
                    return serviceEntity;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


}
