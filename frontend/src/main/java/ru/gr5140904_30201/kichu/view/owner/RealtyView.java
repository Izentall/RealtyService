package ru.gr5140904_30201.kichu.view.owner;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gr5140904_30201.kichu.model.DatesFromTo;
import ru.gr5140904_30201.kichu.model.Realty;
import ru.gr5140904_30201.kichu.model.RealtyAvailability;
import ru.gr5140904_30201.kichu.service.RealtyService;
import ru.gr5140904_30201.kichu.util.AuthUtils;
import ru.gr5140904_30201.kichu.util.AuthorityRequired;
import ru.gr5140904_30201.kichu.util.NavbarUpdatable;
import ru.gr5140904_30201.kichu.view.MainLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.gr5140904_30201.kichu.model.enums.Role.ADMIN;

@Route(value = "realty", layout = MainLayout.class)
@PageTitle("Realty | Real Estate Management")
public class RealtyView extends VerticalLayout implements AuthorityRequired, NavbarUpdatable {
    private final RealtyService realtyService;
    private final Grid<Realty> grid = new Grid<>(Realty.class);
    private final Binder<Realty> binder = new Binder<>(Realty.class);

    private List<Realty> realtyList;
    private Realty currentRealty;

    public RealtyView(@Autowired RealtyService realtyService) {
        this.realtyService = realtyService;
        if (checkRole()) {
            configureGrid();
            add(getToolbar(), grid);
            updateList();
        }
    }

    private void configureGrid() {
        grid.setColumns("title", "description", "address", "pricePerDay");
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                editRealty(event.getValue());
            }
        });
    }

    private HorizontalLayout getToolbar() {
        Button addRealtyButton = new Button("Add realty");
        TextField textField = new TextField("Title for find");
        Button findRealtyButton = new Button("Find");
        addRealtyButton.addClickListener(event -> addRealty());
        findRealtyButton.addClickListener(event -> findRealty(textField));
        return new HorizontalLayout(addRealtyButton, textField, findRealtyButton);
    }

    private void updateList() {
        realtyList = realtyService.getRealtyByOwner(getCurrentUserId());
        grid.setItems(realtyList);
    }

    private void addRealty() {
        currentRealty = new Realty();
        currentRealty.setAvailability(new RealtyAvailability(new ArrayList<>()));
        openEditor(new Dialog(), "Add realty");
    }

    private void editRealty(Realty realty) {
        currentRealty = realty;
        openEditor(new Dialog(), "Edit realty");
    }

    private void findRealty(TextField textField) {
        String title = textField.getValue();
        currentRealty = realtyList.stream()
                .filter(realty -> realty.getTitle().equals(title))
                .findFirst()
                .orElse(null);
        if (currentRealty == null) {
            Notification.show("No realty with title " + title);
        } else {
            editRealty(currentRealty);
        }
    }

    private void openEditor(Dialog dialog, String title) {
        dialog.add(new Div(new Text(title)));

        TextField nameField = new TextField("Title");
        binder.forField(nameField).bind("title");

        TextArea descriptionArea = new TextArea("Description");
        binder.forField(descriptionArea).bind("description");
        descriptionArea.setWidthFull();
        descriptionArea.setHeight("300px");

        TextField addressField = new TextField("Address");
        binder.forField(addressField).bind("address");

        TextField pricePerDayField = new TextField("Price for day (RUB)");
        pricePerDayField.getElement().setAttribute("pattern", "\\d*");
        binder.forField(pricePerDayField)
                .withNullRepresentation("")
                .withConverter(new StringToLongConverter("Only number"))
                .bind("pricePerDay");

        // Управление доступностью
        Grid<DatesFromTo> availabilityGrid = new Grid<>(DatesFromTo.class, false);
        availabilityGrid.addColumn(DatesFromTo::getFrom).setHeader("From");
        availabilityGrid.addColumn(DatesFromTo::getTo).setHeader("To");
        availabilityGrid.setItems(currentRealty.getAvailability().getAvailableIntervals());

        Button addIntervalButton = new Button("Add Interval", event -> addInterval(availabilityGrid));
        Button removeIntervalButton = new Button("Remove Selected Interval", event -> removeInterval(availabilityGrid));

        // Кнопки для сохранения, удаления и отмены
        Button saveButton = new Button("Save", e -> saveRealty(dialog));
        Button deleteButton = new Button("Remove", e -> deleteRealty(dialog));
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(
                new HorizontalLayout(nameField, pricePerDayField, addressField),
                descriptionArea,
                new VerticalLayout(
                        new Div(new Text("Availability:")),
                        availabilityGrid,
                        new HorizontalLayout(addIntervalButton, removeIntervalButton)
                ),
                new HorizontalLayout(saveButton, deleteButton, cancelButton)
        );

        binder.readBean(currentRealty);
        dialog.open();
    }

    private void addInterval(Grid<DatesFromTo> grid) {
        Dialog intervalDialog = new Dialog();

        // Создаем DatePicker для выбора дат
        DatePicker fromDatePicker = new DatePicker("From");
        DatePicker toDatePicker = new DatePicker("To");

        Button saveButton = new Button("Save", event -> {
            try {
                LocalDate from = fromDatePicker.getValue();
                LocalDate to = toDatePicker.getValue();

                if (from != null && to != null) {
                    if (from.isAfter(to)) {
                        Notification.show("From date cannot be after To date.");
                        return;
                    }

                    DatesFromTo interval = new DatesFromTo(from, to);
                    currentRealty.getAvailability().getAvailableIntervals().add(interval);
                    grid.setItems(currentRealty.getAvailability().getAvailableIntervals());
                    intervalDialog.close();
                } else {
                    Notification.show("Please select both dates.");
                }

            } catch (Exception e) {
                Notification.show("Invalid date format.");
            }
        });

        Button cancelButton = new Button("Cancel", event -> intervalDialog.close());
        intervalDialog.add(new HorizontalLayout(fromDatePicker, toDatePicker), new HorizontalLayout(saveButton, cancelButton));
        intervalDialog.open();
    }

    private void removeInterval(Grid<DatesFromTo> grid) {
        DatesFromTo selected = grid.asSingleSelect().getValue();
        if (selected != null) {
            currentRealty.getAvailability().getAvailableIntervals().remove(selected);
            grid.setItems(currentRealty.getAvailability().getAvailableIntervals());
            Notification.show("Interval removed.");
        } else {
            Notification.show("Select an interval to remove.");
        }
    }

    private void saveRealty(Dialog dialog) {
        if (binder.writeBeanIfValid(currentRealty)) {
            if (currentRealty.getId() == null) {
                currentRealty.setOwnerId(getCurrentUserId());
                realtyService.addRealty(currentRealty);
                Notification.show("Realty added.");
            } else {
                realtyService.updateRealty(currentRealty);
                Notification.show("Realty updated.");
            }
            dialog.close();
            updateList();
        }
    }

    private void deleteRealty(Dialog dialog) {
        Realty selectedRealty = grid.asSingleSelect().getValue();
        if (selectedRealty != null) {
            realtyService.deleteRealty(selectedRealty.getId(), getCurrentUserId());
            Notification.show("Realty deleted.");
            updateList();
        } else {
            Notification.show("Choose realty to remove.");
        }
        dialog.close();
    }

    private Long getCurrentUserId() {
        return AuthUtils.getUser().getId();
    }

    @Override
    public String getRequiredRole() {
        return ADMIN;
    }
}
