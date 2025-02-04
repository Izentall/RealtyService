package ru.gr5140904_30201.kichu.service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.gr5140904_30201.kichu.model.*;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RealtyApiClient {
    private final WebClient apiWebClient;

    // Регистрация пользователя
    public String register(User user) {
        return apiWebClient.post()
                .uri("/api/auth/register")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // Вход пользователя
    public User login(User loginRequest) {
        return apiWebClient.post()
                .uri("/api/auth/login")
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

    // Добавление недвижимости
    public Realty addRealty(Realty realty) {
        return apiWebClient.post()
                .uri("/api/realty")
                .bodyValue(realty)
                .retrieve()
                .bodyToMono(Realty.class)
                .block();
    }

    // Обновление недвижимости
    public void updateRealty(Realty realty) {
        apiWebClient.put()
                .uri("/api/realty")
                .bodyValue(realty)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Удаление недвижимости
    public void deleteRealty(Long id, Long ownerId) {
        apiWebClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/realty/{id}/{ownerId}")
                        .build(id, ownerId))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Получение недвижимости по идентификатору владельца
    public List<Realty> getRealtyByOwner(Long ownerId) {
        return apiWebClient.get()
                .uri("/api/realty/owner/{ownerId}", ownerId)
                .retrieve()
                .bodyToFlux(Realty.class)
                .collectList()
                .block();
    }

    // Получение недвижимости по идентификаторам
    public List<Realty> getRealtyByIds(List<Long> ids) {
        return apiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/realty")
                        .queryParam("ids", ids)
                        .build())
                .retrieve()
                .bodyToFlux(Realty.class)
                .collectList()
                .block();
    }

    // Получение бронирований по ID владельца
    public List<Booking> getBookingsByOwnerId(Long ownerId) {
        return apiWebClient.get()
                .uri("/api/bookings/owner/{ownerId}", ownerId)
                .retrieve()
                .bodyToFlux(Booking.class)
                .collectList()
                .block();
    }

    // Обновление статуса бронирования
    public void updateBookingStatus(Long bookingId, String status) {
        apiWebClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/bookings/{bookingId}/status")
                        .queryParam("status", status)
                        .build(bookingId))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Получение статистики бронирования
    public List<BookingStatistics> getBookingStatistics(Long ownerId) {
        return apiWebClient.get()
                .uri("/api/bookings/statistics/{ownerId}", ownerId)
                .retrieve()
                .bodyToFlux(BookingStatistics.class)
                .collectList()
                .block();
    }

    // Получение истории бронирований пользователя
    public List<Booking> getUserBookingHistory(Long userId) {
        return apiWebClient.get()
                .uri("/api/bookings/user/{userId}/history", userId)
                .retrieve()
                .bodyToFlux(Booking.class)
                .collectList()
                .block();
    }

    public Booking createBooking(Booking bookingRequest) {
        return apiWebClient.post()
                .uri("/api/bookings/create")
                .bodyValue(bookingRequest)
                .retrieve()
                .bodyToMono(Booking.class)
                .block();
    }

    // Обновление дат бронирования пользователя
    public void updateUserBooking(Long bookingId, LocalDate startDate, LocalDate endDate) {
        apiWebClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/bookings/{bookingId}/dates")
                        .queryParam("startDate", startDate.toString())
                        .queryParam("endDate", endDate.toString())
                        .build(bookingId))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Отмена бронирования пользователя
    public void cancelUserBooking(Long bookingId) {
        apiWebClient.delete()
                .uri("/api/bookings/{bookingId}", bookingId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Поиск подходящих объектов по критериям
    public List<Realty> searchProperties(PropertySearchCriteria criteria) {
        return apiWebClient.post()
                .uri("/api/realty/search")
                .bodyValue(criteria)
                .retrieve()
                .bodyToFlux(Realty.class)
                .collectList()
                .block();
    }
}
