package ru.gr5140904_30201.kichu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gr5140904_30201.kichu.model.PropertySearchCriteria;
import ru.gr5140904_30201.kichu.model.Realty;
import ru.gr5140904_30201.kichu.service.client.RealtyApiClient;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RealtyService {
    private final RealtyApiClient realtyApiClient;

    public Realty addRealty(Realty realty) {
        return realtyApiClient.addRealty(realty);
    }

    public void updateRealty(Realty realty) {
        realtyApiClient.updateRealty(realty);
    }

    public void deleteRealty(Long id, Long ownerId) {
        realtyApiClient.deleteRealty(id, ownerId);
    }

    public List<Realty> getRealtyByOwner(Long ownerId) {
        return realtyApiClient.getRealtyByOwner(ownerId);
    }

    public List<Realty> getRealtyByIds(List<Long> ids) {
        return realtyApiClient.getRealtyByIds(ids);
    }

    public List<Realty> searchProperties(
            String location,
            LocalDate startDate,
            LocalDate endDate,
            Long minPrice,
            Long maxPrice,
            String propertyType
    ) {
        return realtyApiClient.searchProperties(new PropertySearchCriteria(
                location,
                startDate,
                endDate,
                minPrice,
                maxPrice,
                propertyType
        ));
    }
}
