package ru.gr5140904_30201.kichu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gr5140904_30201.kichu.model.Booking;
import ru.gr5140904_30201.kichu.model.BookingStatistics;
import ru.gr5140904_30201.kichu.serivce.BookingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    // Получение бронирований по ID владельца
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Booking>> getBookingsByOwnerId(@PathVariable Long ownerId) {
        List<Booking> bookings = bookingService.getBookingsByOwnerId(ownerId);
        if (bookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(bookings);
        }
        return ResponseEntity.ok(bookings);
    }

    // Обновление статуса бронирования
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<Void> updateBookingStatus(@PathVariable Long bookingId, @RequestParam String status) {
        bookingService.updateBookingStatus(bookingId, status);
        return ResponseEntity.ok().build();
    }

    // Получение статистики бронирования
    @GetMapping("/statistics/{ownerId}")
    public ResponseEntity<List<BookingStatistics>> getBookingStatistics(@PathVariable Long ownerId) {
        List<BookingStatistics> statistics = bookingService.getBookingStatistics(ownerId);
        if (statistics.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(statistics);
        }
        return ResponseEntity.ok(statistics);
    }

    // Получение истории бронирований пользователя
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<Booking>> getUserBookingHistory(@PathVariable Long userId) {
        List<Booking> bookingHistory = bookingService.getUserBookingHistory(userId);
        if (bookingHistory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(bookingHistory);
        }
        return ResponseEntity.ok(bookingHistory);
    }

    // Добавление нового бронирования
    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        Booking newBooking = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
    }

    // Обновление дат бронирования пользователя
    @PutMapping("/{bookingId}/dates")
    public ResponseEntity<Void> updateUserBooking(
            @PathVariable Long bookingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        bookingService.updateUserBooking(bookingId, startDate, endDate);
        return ResponseEntity.ok().build();
    }

    // Отмена бронирования пользователя
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelUserBooking(@PathVariable Long bookingId) {
        bookingService.cancelUserBooking(bookingId);
        return ResponseEntity.ok().build();
    }
}
