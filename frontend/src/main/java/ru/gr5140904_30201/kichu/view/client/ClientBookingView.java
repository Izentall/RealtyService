package ru.gr5140904_30201.kichu.view.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gr5140904_30201.kichu.model.Booking;
import ru.gr5140904_30201.kichu.model.Realty;
import ru.gr5140904_30201.kichu.model.enums.BookingStatus;
import ru.gr5140904_30201.kichu.service.BookingService;
import ru.gr5140904_30201.kichu.service.RealtyService;
import ru.gr5140904_30201.kichu.util.AuthUtils;
import ru.gr5140904_30201.kichu.util.AuthorityRequired;
import ru.gr5140904_30201.kichu.util.NavbarUpdatable;
import ru.gr5140904_30201.kichu.view.MainLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.gr5140904_30201.kichu.model.enums.Role.USER;

@Route(value = "booking", layout = MainLayout.class)
@PageTitle("Booking | Real Estate Management")
public class ClientBookingView extends VerticalLayout implements AuthorityRequired, NavbarUpdatable {
    private final BookingService bookingService;
    private final RealtyService realtyService;
    private final DatePicker startDatePicker = new DatePicker("Start Date");
    private final DatePicker endDatePicker = new DatePicker("End Date");
    private final Grid<Realty> searchResultsGrid = new Grid<>(Realty.class);
    private final Grid<Booking> bookingGrid = new Grid<>(Booking.class);

    private List<Realty> foundRealties = new ArrayList<>();

    @Autowired
    public ClientBookingView(BookingService bookingService, RealtyService realtyService) {
        this.bookingService = bookingService;
        this.realtyService = realtyService;

        if (checkRole()) {
            VerticalLayout content = new VerticalLayout();
            content.add(configureSearchSection(), new H1("Found objects"));
            add(content);
            configureSearchResultsGrid();
            add(new H1("Your bookings"));
            configureBookingGrid();
            loadBookings();
        }
    }

    private HorizontalLayout configureSearchSection() {
        TextField locationField = new TextField("Location");
        TextField minPriceField = new TextField("Min Price");
        TextField maxPriceField = new TextField("Max Price");

        Button searchButton = new Button("Search", event -> {
            String location = locationField.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            Long minPrice = minPriceField.getValue().isEmpty() ? null : Long.valueOf(minPriceField.getValue());
            Long maxPrice = maxPriceField.getValue().isEmpty() ? null : Long.valueOf(maxPriceField.getValue());

            foundRealties = realtyService.searchProperties(
                    location, startDate, endDate, minPrice, maxPrice, null);
            searchResultsGrid.setItems(foundRealties);
        });

        return new HorizontalLayout(locationField, startDatePicker, endDatePicker, minPriceField, maxPriceField, searchButton);
    }

    private void configureSearchResultsGrid() {
        searchResultsGrid.removeAllColumns();
        searchResultsGrid.setWidthFull();
        searchResultsGrid.addColumn(Realty::getTitle)
                .setHeader("Property Title")
                .setAutoWidth(true);

        searchResultsGrid.addColumn(Realty::getAddress)
                .setHeader("Address")
                .setAutoWidth(true);

        searchResultsGrid.addColumn(Realty::getDescription)
                .setHeader("Description")
                .setAutoWidth(true);

        searchResultsGrid.addColumn(Realty::getPricePerDay)
                .setHeader("Price per day (kop)")
                .setAutoWidth(true);

        searchResultsGrid.addComponentColumn(realty -> {
            Button bookButton = new Button("Book", event -> {
                if (endDatePicker.isEmpty() || startDatePicker.isEmpty()
                        || endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
                    Notification.show("End date can not be before than Start date");
                    return;
                }
                Booking booking = Booking.builder()
                        .propertyId(realty.getId())
                        .userId(getUserId())
                        .startDate(startDatePicker.getValue())
                        .endDate(endDatePicker.getValue())
                        .status(BookingStatus.PENDING)
                        .build();

                bookingService.createBooking(booking);
                foundRealties.remove(realty);
                searchResultsGrid.setItems(foundRealties);
                Notification.show("Booking created successfully!");
                loadBookings();
            });
            return bookButton;
        }).setHeader("Actions").setAutoWidth(true);

        Div gridContainer = new Div();
        gridContainer.add(searchResultsGrid);
        gridContainer.getStyle().set("overflow-x", "auto");
        gridContainer.setWidthFull();
        add(gridContainer);
    }

    private void configureBookingGrid() {
        bookingGrid.removeAllColumns();
        bookingGrid.addColumn(booking -> {
            return realtyService.getRealtyByIds(List.of(booking.getPropertyId())).stream()
                    .map(Realty::getTitle)
                    .findFirst()
                    .orElse("Unknown");
        }).setHeader("Property Title").setAutoWidth(false).setFlexGrow(0);

        bookingGrid.addColumn(booking -> {
            return realtyService.getRealtyByIds(List.of(booking.getPropertyId())).stream()
                    .map(Realty::getPricePerDay)
                    .findFirst()
                    .orElseThrow();
        }).setHeader("Price per day (RUB)").setAutoWidth(false).setFlexGrow(0);

        bookingGrid.addColumn(Booking::getStartDate)
                .setHeader("Start Date")
                .setAutoWidth(false)
                .setFlexGrow(0);

        bookingGrid.addColumn(Booking::getEndDate)
                .setHeader("End Date")
                .setAutoWidth(false)
                .setFlexGrow(0);

        bookingGrid.addColumn(Booking::getStatus)
                .setHeader("Status")
                .setAutoWidth(false)
                .setFlexGrow(0);

        bookingGrid.addComponentColumn(booking -> {
            Button cancelButton = new Button("Cancel", click -> {
                bookingService.cancelUserBooking(booking.getId());
                loadBookings();
            });
            return cancelButton;
        }).setHeader("Actions").setAutoWidth(false).setFlexGrow(0);

        bookingGrid.addComponentColumn(booking -> {
            Button editButton = new Button("Edit", click -> {
                Dialog editDialog = createEditBookingDialog(booking);
                editDialog.open();
            });
            return editButton;
        }).setHeader("Edit").setAutoWidth(false).setFlexGrow(0);

        Div gridContainer = new Div();
        gridContainer.add(bookingGrid);
        gridContainer.getStyle().set("overflow-x", "auto");
        gridContainer.setWidthFull();
        add(gridContainer);
    }

    private Dialog createEditBookingDialog(Booking booking) {
        Dialog dialog = new Dialog();
        DatePicker startDatePicker = new DatePicker("Start Date");
        startDatePicker.setValue(booking.getStartDate());
        DatePicker endDatePicker = new DatePicker("End Date");
        endDatePicker.setValue(booking.getEndDate());

        Button saveButton = new Button("Save", event -> {
            bookingService.updateUserBooking(booking.getId(), startDatePicker.getValue(), endDatePicker.getValue());
            Notification.show("Booking updated successfully!");
            dialog.close();
            loadBookings();
        });

        dialog.add(startDatePicker, endDatePicker, saveButton);
        return dialog;
    }

    private void loadBookings() {
        List<Booking> bookings = bookingService.getUserBookingHistory(getUserId());
        List<Long> propertyIds = bookings.stream()
                .map(Booking::getPropertyId)
                .collect(Collectors.toList());

        bookingGrid.removeAllColumns();
        List<Realty> realties = realtyService.getRealtyByIds(propertyIds);

        bookingGrid.addColumn(booking -> {
            return realties.stream()
                    .filter(realty -> realty.getId().equals(booking.getPropertyId()))
                    .map(Realty::getTitle)
                    .findFirst()
                    .orElse("Unknown");
        }).setHeader("Property Title");
        bookingGrid.addColumn(booking -> {
            return realties.stream()
                    .filter(realty -> realty.getId().equals(booking.getPropertyId()))
                    .map(Realty::getPricePerDay)
                    .findFirst()
                    .orElseThrow();
        }).setHeader("Price per day (RUB)");

        bookingGrid.addColumns("startDate", "endDate", "status");

        bookingGrid.setItems(bookings);
    }

    private Long getUserId() {
        return AuthUtils.getUser().getId();
    }

    @Override
    public String getRequiredRole() {
        return USER;
    }
}
