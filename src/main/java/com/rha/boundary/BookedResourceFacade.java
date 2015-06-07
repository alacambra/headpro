package com.rha.boundary;

import com.rha.control.LocalDateConverter;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Service;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alacambra
 */
@Stateless
public class BookedResourceFacade extends AbstractFacade<BookedResource> {

    @PersistenceContext(unitName = "rha")
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<BookedResource> getBookedResourcesForServiceInPeriod(
            Service service, LocalDate startDate, LocalDate endDate) {

        List<BookedResource> bookedResources
                = em.createNamedQuery(BookedResource.forServiceInPeriod)
                .setParameter("service", service)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return bookedResources;
    }

    public List<PeriodTotal> getTotalBookedResourcesForServiceInPeriod(
            Service service, LocalDate startDate, LocalDate endDate) {

        List<PeriodTotal> bookedResources
                = em.createNamedQuery(BookedResource.totalForServiceInPeriod, PeriodTotal.class)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .setParameter("service", service)
                .getResultList();

        return bookedResources;
    }

    public List<PeriodTotal> getTotalBookedResourcesByServiceInPeriod(LocalDate startDate, LocalDate endDate) {

        List<PeriodTotal> bookedResources
                = em.createNamedQuery(BookedResource.totalByServiceInPeriod, PeriodTotal.class)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return bookedResources;
    }

    public BookedResourceFacade() {
        super(BookedResource.class);
    }

    public void updateOrCreateBookings(List<BookedResource> resources) {

        resources.stream().filter((resource) -> (resource.isPersisted() || resource.getBooked() != 0))
                .forEach(br -> {
                    if (br.getId() == null) {
                        em.persist(br);
                    } else {
                        em.merge(br);
                    }
                });
    }

    List<BookedResource> getBookedResourcesInPeriod(LocalDate startDate, LocalDate endDate) {
        List<BookedResource> r = 
                em.createNamedQuery(BookedResource.bookedInPeriod)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();
        
        return r;
    }
}
