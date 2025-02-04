package ru.gr5140904_30201.kichu.serivce;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gr5140904_30201.kichu.dao.RealtyDao;
import ru.gr5140904_30201.kichu.model.PropertySearchCriteria;
import ru.gr5140904_30201.kichu.model.Realty;
import ru.gr5140904_30201.kichu.model.RealtyAvailability;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RealtyService {
    private final RealtyDao realtyDao;

    public void addRealty(Realty realty) {
        if (realty.getAvailability() == null) {
            realty.setAvailability(new RealtyAvailability());
        }
        realtyDao.addRealty(realty);
    }

    public void updateRealty(Realty realty) {
        realtyDao.updateRealty(realty);
    }

    public void deleteRealty(Long id, Long ownerId) {
        realtyDao.deleteRealty(id, ownerId);
    }

    @Transactional(readOnly = true)
    public List<Realty> getRealtyByOwner(Long ownerId) {
        return realtyDao.getRealtyByOwner(ownerId);
    }

    public List<Realty> getRealtyByIds(List<Long> ids) {
        return realtyDao.getRealtyByIds(ids);
    }

    public List<Realty> searchProperties(PropertySearchCriteria criteria) {
        return realtyDao.searchProperties(
                criteria.getLocation(),
                criteria.getStartDate(),
                criteria.getEndDate(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getPropertyType()
        );
    }
}

