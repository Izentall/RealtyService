package ru.gr5140904_30201.kichu.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.gr5140904_30201.kichu.model.Realty;
import ru.gr5140904_30201.kichu.model.RealtyAvailability;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RealtyDao {
    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void addRealty(Realty realty) {
        try {
            namedParameterJdbcTemplate.update("""
                    insert into properties (owner_id, title, description, address, price_per_day, availability)
                    values (:owner_id, :title, :description, :address, :price_per_day, :availability::jsonb)
                    """,
                    new MapSqlParameterSource()
                            .addValue("owner_id", realty.getOwnerId())
                            .addValue("title", realty.getTitle())
                            .addValue("description", realty.getDescription())
                            .addValue("address", realty.getAddress())
                            .addValue("price_per_day", realty.getPricePerDay())
                            .addValue("availability", objectMapper.writeValueAsString(realty.getAvailability()))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRealty(Realty realty) {
        try {
            namedParameterJdbcTemplate.update("""
                    update properties
                    set title = :title, description = :description, address = :address, 
                        price_per_day = :price_per_day, availability = :availability::jsonb
                    where id = :id and owner_id = :owner_id
                    """,
                    new MapSqlParameterSource()
                            .addValue("id", realty.getId())
                            .addValue("owner_id", realty.getOwnerId())
                            .addValue("title", realty.getTitle())
                            .addValue("description", realty.getDescription())
                            .addValue("address", realty.getAddress())
                            .addValue("price_per_day", realty.getPricePerDay())
                            .addValue("availability", objectMapper.writeValueAsString(realty.getAvailability()))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRealty(Long id, Long ownerId) {
        namedParameterJdbcTemplate.update("""
                delete from properties
                where id = :id and owner_id = :owner_id
                """,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("owner_id", ownerId)
        );
    }

    public List<Realty> getRealtyByOwner(Long ownerId) {
        return namedParameterJdbcTemplate.query("""
                select *
                from properties
                where owner_id = :owner_id
                """,
                new MapSqlParameterSource("owner_id", ownerId),
                this::mapRealty
        );
    }

    public List<Realty> getRealtyByIds(List<Long> ids) {
        return namedParameterJdbcTemplate.query("""
                select *
                from properties
                where id in (select unnest(:ids))
                """,
                new MapSqlParameterSource("ids", ids.toArray(Long[]::new)),
                this::mapRealty
        );
    }

    public List<Realty> searchProperties(String location, LocalDate startDate, LocalDate endDate,
                                         Long minPrice, Long maxPrice, String propertyType) {
        String sql = """
        SELECT p.*
        FROM properties p
        WHERE
            address ILIKE :location
            AND price_per_day BETWEEN :minPrice AND :maxPrice
            AND NOT EXISTS (
                SELECT 1
                FROM jsonb_array_elements(p.availability->'availableIntervals') AS intervals
                WHERE
                    (intervals->>'from')::date > :startDate
                    OR (intervals->>'to')::date < :endDate
            )
            AND NOT EXISTS (
                SELECT 1
                FROM bookings b
                WHERE
                    b.property_id = p.id
                    AND b.status = 'approved'
                    AND (
                        (b.start_date BETWEEN :startDate AND :endDate)
                        OR (b.end_date BETWEEN :startDate AND :endDate)
                        OR (:startDate BETWEEN b.start_date AND b.end_date)
                        OR (:endDate BETWEEN b.start_date AND b.end_date)
                    )
            )
        """;

        Map<String, Object> params = new HashMap<>();
        params.put("location", "%" + location + "%");
        params.put("minPrice", minPrice != null ? minPrice : 0);
        params.put("maxPrice", maxPrice != null ? maxPrice : Long.MAX_VALUE);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        if (propertyType != null && !propertyType.isEmpty()) {
            sql += " AND description ILIKE :propertyType";
            params.put("propertyType", "%" + propertyType + "%");
        }

        return namedParameterJdbcTemplate.query(sql, params, this::mapRealty);
    }

    private Realty mapRealty(ResultSet rs, int rowNum) {
        try {
            return Realty.builder()
                    .id(rs.getLong("id"))
                    .ownerId(rs.getLong("owner_id"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .address(rs.getString("address"))
                    .pricePerDay(rs.getLong("price_per_day"))
                    .availability(objectMapper.readValue(rs.getString("availability"), RealtyAvailability.class))
                    .build();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
