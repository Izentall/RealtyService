package ru.gr5140904_30201.kichu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gr5140904_30201.kichu.model.Booking;
import ru.gr5140904_30201.kichu.model.BookingStatistics;
import ru.gr5140904_30201.kichu.service.client.RealtyApiClient;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final RealtyApiClient realtyApiClient;

    public List<Booking> getBookingsByOwnerId(Long ownerId) {
        return realtyApiClient.getBookingsByOwnerId(ownerId);
    }

    public void updateBookingStatus(Long bookingId, String status) {
        realtyApiClient.updateBookingStatus(bookingId, status);
    }

    public List<BookingStatistics> getBookingStatistics(Long ownerId) {
        return realtyApiClient.getBookingStatistics(ownerId);
    }

    public List<Booking> getUserBookingHistory(Long userId) {
        return realtyApiClient.getUserBookingHistory(userId);
    }

    public Booking createBooking(Booking booking) {
        return realtyApiClient.createBooking(booking);
    }

    public void updateUserBooking(Long bookingId, LocalDate startDate, LocalDate endDate) {
        realtyApiClient.updateUserBooking(bookingId, startDate, endDate);
    }

    public void cancelUserBooking(Long bookingId) {
        realtyApiClient.cancelUserBooking(bookingId);
    }
}
