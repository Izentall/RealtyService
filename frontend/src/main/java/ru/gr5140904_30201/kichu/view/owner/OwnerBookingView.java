package ru.gr5140904_30201.kichu.view.owner;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gr5140904_30201.kichu.model.Booking;
import ru.gr5140904_30201.kichu.model.BookingStatistics;
import ru.gr5140904_30201.kichu.model.Realty;
import ru.gr5140904_30201.kichu.model.enums.BookingStatus;
import ru.gr5140904_30201.kichu.service.BookingService;
import ru.gr5140904_30201.kichu.service.RealtyService;
import ru.gr5140904_30201.kichu.util.AuthUtils;
import ru.gr5140904_30201.kichu.util.AuthorityRequired;
import ru.gr5140904_30201.kichu.util.NavbarUpdatable;
import ru.gr5140904_30201.kichu.view.MainLayout;

import java.util.List;

import static ru.gr5140904_30201.kichu.model.enums.Role.ADMIN;

@Route(value = "booking-approve", layout = MainLayout.class)
@PageTitle("Booking approve | Real Estate Management")
public class OwnerBookingView extends VerticalLayout implements AuthorityRequired, NavbarUpdatable {

    private final BookingService bookingService;
    private final RealtyService realtyService;
    private final Grid<Booking> bookingGrid = new Grid<>(Booking.class);
    private final Grid<BookingStatistics> statisticsGrid = new Grid<>(BookingStatistics.class);

    @Autowired
    public OwnerBookingView(BookingService bookingService, RealtyService realtyService) {
        this.bookingService = bookingService;
        this.realtyService = realtyService;
        if (checkRole()) {
            Tabs tabs = new Tabs(new Tab("Bookings"), new Tab("Statistics"));
            VerticalLayout content = new VerticalLayout();
            content.add(bookingGrid);

            configureBookingGrid();
            configureStatisticsGrid();

            tabs.addSelectedChangeListener(event -> {
                content.removeAll();
                if (event.getSelectedTab().getLabel().equals("Bookings")) {
                    content.add(bookingGrid);
                    updateBookings();
                } else {
                    content.add(statisticsGrid);
                    updateStatistics();
                }
            });

            add(tabs, content);
            updateBookings();
        }
    }

    private void configureBookingGrid() {
        // Получаем список объектов недвижимости с их названиями
        List<Realty> realties = realtyService.getRealtyByOwner(getOwnerId());
        bookingGrid.removeAllColumns();

        // Замена отображения propertyId на title
        bookingGrid.addColumn(booking -> {
            return realties.stream()
                    .filter(realty -> realty.getId().equals(booking.getPropertyId()))
                    .map(Realty::getTitle)
                    .findFirst()
                    .orElse("Unknown");
        }).setHeader("Property Title");

        // Настраиваем отображение колонок
        bookingGrid.addColumns("userId", "startDate", "endDate", "status");

        // Добавляем в каждую строку кнопки для изменения статуса
        bookingGrid.addComponentColumn(booking -> {
            Button approveButton = new Button("Approve", click -> updateStatus(booking.getId(), BookingStatus.APPROVED));
            Button declineButton = new Button("Decline", click -> updateStatus(booking.getId(), BookingStatus.DECLINED));
            approveButton.setEnabled(booking.getStatus() == BookingStatus.PENDING);
            declineButton.setEnabled(booking.getStatus() == BookingStatus.PENDING);
            return new VerticalLayout(approveButton, declineButton);
        }).setHeader("Actions");
    }

    private void configureStatisticsGrid() {
        // Получаем список объектов недвижимости с их названиями
        List<Realty> realties = realtyService.getRealtyByOwner(getOwnerId());
        statisticsGrid.removeAllColumns();
        // Замена отображения propertyId на title
        statisticsGrid.addColumn(bookingStatistics -> {
            return realties.stream()
                    .filter(realty -> realty.getId().equals(bookingStatistics.getPropertyId()))
                    .map(Realty::getTitle)
                    .findFirst()
                    .orElse("Unknown");
        }).setHeader("Property Title");

        statisticsGrid.addColumns("totalBookings", "totalDaysBooked");
    }

    private void updateBookings() {
        List<Booking> bookings = bookingService.getBookingsByOwnerId(getOwnerId());
        bookingGrid.setItems(bookings);
    }

    private void updateStatistics() {
        List<BookingStatistics> statistics = bookingService.getBookingStatistics(getOwnerId());
        statisticsGrid.setItems(statistics);
    }

    private void updateStatus(Long bookingId, BookingStatus status) {
        if (status == BookingStatus.APPROVED || status == BookingStatus.DECLINED) {
            bookingService.updateBookingStatus(bookingId, status.name());
            updateBookings();
            Notification.show("Booking status updated to " + status.name(), 3000, Notification.Position.BOTTOM_START);
        }
    }

    private Long getOwnerId() {
        return AuthUtils.getUser().getId();
    }

    @Override
    public String getRequiredRole() {
        return ADMIN;
    }
}
