package ru.gr5140904_30201.kichu.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.gr5140904_30201.kichu.model.Booking;
import ru.gr5140904_30201.kichu.model.BookingStatistics;
import ru.gr5140904_30201.kichu.model.enums.BookingStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Booking> getBookingsByOwnerId(Long ownerId) {
        return namedParameterJdbcTemplate.query("""
                select b.*
                from bookings b
                join properties p on b.property_id = p.id
                where p.owner_id = :owner_id
                """,
                new MapSqlParameterSource("owner_id", ownerId),
                BookingDao::mapBooking
        );
    }

    public List<Booking> getBookingsByPropertyId(Long propertyId) {
        return namedParameterJdbcTemplate.query("""
                select *
                from bookings b
                where property_id = :property_id
                """,
                new MapSqlParameterSource("property_id", propertyId),
                BookingDao::mapBooking
        );
    }

    public void updateBookingStatus(Long bookingId, String status) {
        namedParameterJdbcTemplate.update("""
                update bookings
                set status = :status::booking_status
                where id = :booking_id
                """,
                new MapSqlParameterSource()
                        .addValue("booking_id", bookingId)
                        .addValue("status", status.toLowerCase())
        );
    }

    public List<BookingStatistics> getBookingStatistics(Long ownerId) {
        return namedParameterJdbcTemplate.query("""
                select
                    p.id as property_id,
                    p.title, count(b.id) as total_bookings,
                    sum(b.end_date - b.start_date + 1) as total_days_booked
                from properties p
                left join bookings b on p.id = b.property_id
                where p.owner_id = :owner_id
                    and b.status = 'approved'
                group by p.id, p.title
                """,
                new MapSqlParameterSource("owner_id", ownerId),
                BookingDao::mapBookingStatistics
        );
    }

    public List<Booking> getUserBookingHistory(Long userId) {
        return namedParameterJdbcTemplate.query("""
                select *
                from bookings
                where user_id = :user_id
                order by created_at desc
                """,
                new MapSqlParameterSource("user_id", userId),
                BookingDao::mapBooking
        );
    }

    public void updateUserBooking(Long bookingId, LocalDate startDate, LocalDate endDate) {
        namedParameterJdbcTemplate.update("""
                update bookings
                set start_date = :start_date, end_date = :end_date
                where id = :booking_id
                """,
                new MapSqlParameterSource()
                        .addValue("booking_id", bookingId)
                        .addValue("start_date", startDate)
                        .addValue("end_date", endDate)
        );
    }

    public Booking createBooking(Booking booking) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update("""
                insert into bookings (user_id, property_id, start_date, end_date, status)
                    values (:user_id, :property_id, :start_date, :end_date, :status::booking_status)
                returning id
                """,
                new MapSqlParameterSource()
                        .addValue("user_id", booking.getUserId())
                        .addValue("property_id", booking.getPropertyId())
                        .addValue("start_date", booking.getStartDate())
                        .addValue("end_date", booking.getEndDate())
                        .addValue("status", booking.getStatus().toString().toLowerCase()),
                keyHolder
        );
        booking.setId((Long) keyHolder.getKey());
        return booking;
    }

    public void cancelUserBooking(Long bookingId) {
        namedParameterJdbcTemplate.update("""
                delete from bookings
                where id = :booking_id
                """,
                new MapSqlParameterSource("booking_id", bookingId)
        );
    }

    private static Booking mapBooking(ResultSet rs, int rowNum) {
        try {
            return Booking.builder()
                    .id(rs.getLong("id"))
                    .userId(rs.getLong("user_id"))
                    .propertyId(rs.getLong("property_id"))
                    .startDate(LocalDate.parse(rs.getString("start_date")))
                    .endDate(LocalDate.parse(rs.getString("end_date")))
                    .status(BookingStatus.valueOf(rs.getString("status").toUpperCase()))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static BookingStatistics mapBookingStatistics(ResultSet rs, int rowNum) {
        try {
            return BookingStatistics.builder()
                    .propertyId(rs.getLong("property_id"))
                    .totalBookings(rs.getLong("total_bookings"))
                    .totalDaysBooked(rs.getLong("total_days_booked"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
