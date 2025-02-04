package ru.gr5140904_30201.kichu.serivce;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gr5140904_30201.kichu.dao.BookingDao;
import ru.gr5140904_30201.kichu.model.Booking;
import ru.gr5140904_30201.kichu.model.BookingStatistics;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingDao bookingDao;

    public List<Booking> getBookingsByOwnerId(Long ownerId) {
        return bookingDao.getBookingsByOwnerId(ownerId);
    }

    public void updateBookingStatus(Long bookingId, String status) {
        bookingDao.updateBookingStatus(bookingId, status);
    }

    public List<BookingStatistics> getBookingStatistics(Long ownerId) {
        return bookingDao.getBookingStatistics(ownerId);
    }

    public List<Booking> getUserBookingHistory(Long userId) {
        return bookingDao.getUserBookingHistory(userId);
    }

    public Booking createBooking(Booking booking) {
        return bookingDao.createBooking(booking);
    }

    public void updateUserBooking(Long bookingId, LocalDate startDate, LocalDate endDate) {
        bookingDao.updateUserBooking(bookingId, startDate, endDate);
    }

    public void cancelUserBooking(Long bookingId) {
        bookingDao.cancelUserBooking(bookingId);
    }
}
